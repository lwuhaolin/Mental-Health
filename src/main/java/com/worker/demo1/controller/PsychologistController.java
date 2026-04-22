package com.worker.demo1.controller;

import com.worker.demo1.entity.Psychologist;
import com.worker.demo1.entity.Consultation;
import com.worker.demo1.service.PsychologistService;
import com.worker.demo1.service.ConsultationService;
import com.worker.demo1.mapper.PsychologistMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.io.File;
import java.io.IOException;

/**
 * 心理专家控制器
 */
@RestController
@RequestMapping("/psychologist")
public class PsychologistController {
    
    @Autowired
    private PsychologistService psychologistService;
    
    @Autowired
    private ConsultationService consultationService;
    
    @Autowired
    private PsychologistMapper psychologistMapper;
    
    /**
     * 专家登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        // 先查询状态，禁用则直接提示（容忍重复数据，不抛异常）
        Psychologist exists = psychologistService.getOne(
            new LambdaQueryWrapper<Psychologist>().eq(Psychologist::getUsername, username),
            false
        );
        if (exists != null) {
            String s = exists.getStatus();
            boolean isNormal = s != null && ("正常".equals(s) || "1".equals(s) || s.contains("正"));
            if (!isNormal) {
                result.put("success", false);
                result.put("message", "账号已禁用，无法登录");
                return result;
            }
        }
        
        Psychologist psychologist = psychologistService.login(username, password);
        if (psychologist != null) {
            result.put("success", true);
            result.put("message", "登录成功");
            
            // 手动构建返回数据，避免时间字段序列化问题
            Map<String, Object> psychologistData = new HashMap<>();
            psychologistData.put("id", psychologist.getId());
            psychologistData.put("username", psychologist.getUsername());
            psychologistData.put("realName", psychologist.getRealName());
            psychologistData.put("title", psychologist.getTitle());
            psychologistData.put("specialty", psychologist.getSpecialty());
            psychologistData.put("experienceYears", psychologist.getExperienceYears());
            psychologistData.put("introduction", psychologist.getIntroduction());
            psychologistData.put("avatar", psychologist.getAvatar());
            psychologistData.put("email", psychologist.getEmail());
            psychologistData.put("phone", psychologist.getPhone());
            psychologistData.put("hourlyRate", psychologist.getHourlyRate());
            psychologistData.put("rating", psychologist.getRating());
            psychologistData.put("status", psychologist.getStatus());
            psychologistData.put("lastLoginTime", psychologist.getLastLoginTime());
            psychologistData.put("createTime", psychologist.getCreateTime() != null ? 
                psychologist.getCreateTime().toString() : null);
            psychologistData.put("updateTime", psychologist.getUpdateTime() != null ? 
                psychologist.getUpdateTime().toString() : null);
            
            result.put("psychologist", psychologistData);
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }
    
    /**
     * 获取专家列表（支持搜索）
     */
    @GetMapping("/list")
    public Map<String, Object> getPsychologistList(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String sortBy) {
        Map<String, Object> result = new HashMap<>();
        List<Psychologist> psychologists = psychologistService.searchPsychologists(keyword, specialty, sortBy);
        result.put("success", true);
        result.put("psychologists", psychologists);
        return result;
    }
    
    /**
     * 根据擅长领域查询专家
     */
    @GetMapping("/search")
    public Map<String, Object> searchBySpecialty(@RequestParam String specialty) {
        Map<String, Object> result = new HashMap<>();
        List<Psychologist> psychologists = psychologistService.getPsychologistsBySpecialty(specialty);
        result.put("success", true);
        result.put("psychologists", psychologists);
        return result;
    }
    
    /**
     * 获取专家详情
     */
    @GetMapping("/detail/{id}")
    public Map<String, Object> getPsychologistDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        Psychologist psychologist = psychologistService.getById(id);
        if (psychologist != null) {
            result.put("success", true);
            result.put("psychologist", psychologist);
        } else {
            result.put("success", false);
            result.put("message", "专家不存在");
        }
        return result;
    }
    
    /**
     * 获取专家患者列表
     */
    @GetMapping("/patients/{id}")
    public Map<String, Object> getPsychologistPatients(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> patients = consultationService.getPsychologistPatients(id);
            result.put("success", true);
            result.put("patients", patients);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取患者列表失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 根据用户ID获取该用户的咨询记录
     */
    @GetMapping("/user-consultations/{userId}")
    public Map<String, Object> getUserConsultations(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Consultation> consultations = consultationService.getConsultationsByUserId(userId);
            result.put("success", true);
            result.put("consultations", consultations);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户咨询记录失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取专家统计数据
     */
    @GetMapping("/stats/{id}")
    public Map<String, Object> getPsychologistStats(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 获取专家基本信息
            Psychologist psychologist = psychologistService.getById(id);
            if (psychologist == null) {
                result.put("success", false);
                result.put("message", "专家不存在");
                return result;
            }
            
            // 获取咨询记录统计
            List<Consultation> consultations = consultationService.getPsychologistConsultations(id);
            
            // 计算统计数据
            int patientCount = (int) consultations.stream()
                    .map(Consultation::getUserId)
                    .distinct()
                    .count();
            
            int consultationCount = consultations.size();
            
            double avgRating = consultations.stream()
                    .filter(c -> c.getUserRating() != null)
                    .mapToInt(Consultation::getUserRating)
                    .average()
                    .orElse(psychologist.getRating() != null ? psychologist.getRating().doubleValue() : 4.5);
            
            // 从数据库account字段获取总收入
            double totalIncome = psychologist.getAccount() != null ? 
                psychologist.getAccount().doubleValue() : 0.0;
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("patientCount", patientCount);
            stats.put("consultationCount", consultationCount);
            stats.put("avgRating", Math.round(avgRating * 10) / 10.0); // 保留一位小数
            stats.put("totalIncome", Math.round(totalIncome * 100) / 100.0); // 保留两位小数
            
            result.put("success", true);
            result.put("stats", stats);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计数据失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 心理专家注册
     */
    @PostMapping("/register")
    public Map<String, Object> register(@RequestBody Psychologist psychologist) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 验证必填字段
            if (psychologist.getUsername() == null || psychologist.getUsername().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "用户名不能为空");
                return result;
            }
            
            if (psychologist.getPassword() == null || psychologist.getPassword().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "密码不能为空");
                return result;
            }
            
            if (psychologist.getRealName() == null || psychologist.getRealName().trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "真实姓名不能为空");
                return result;
            }
            
            // 调用服务层注册
            boolean registerResult = psychologistService.register(psychologist);
            
            if (registerResult) {
                result.put("success", true);
                result.put("message", "心理专家注册成功，请等待管理员审核");
            } else {
                result.put("success", false);
                result.put("message", "用户名已存在，请更换用户名");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "注册失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 脑科专家忘记密码
     */
    @PostMapping("/forgot-password")
    public Map<String, Object> forgotPassword(@RequestBody Map<String, String> passwordData) {
        Map<String, Object> result = new HashMap<>();
        try {
            String username = passwordData.get("username");
            String email = passwordData.get("email");
            String newPassword = passwordData.get("newPassword");
            
            // 验证必填字段
            if (username == null || username.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "用户名不能为空");
                return result;
            }
            
            if (email == null || email.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "邮箱不能为空");
                return result;
            }
            
            if (newPassword == null || newPassword.trim().isEmpty()) {
                result.put("success", false);
                result.put("message", "新密码不能为空");
                return result;
            }
            
            // 调用服务层重置密码
            boolean success = psychologistService.resetPassword(username, email, newPassword);
            if (success) {
                result.put("success", true);
                result.put("message", "密码重置成功");
            } else {
                result.put("success", false);
                result.put("message", "用户名和邮箱不匹配或专家状态异常");
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "密码重置失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 上传并更新专家头像
     */
    @PostMapping("/avatar/upload")
    public Map<String, Object> uploadAvatar(@RequestParam("id") Long id,
                                            @RequestParam("file") MultipartFile file,
                                            jakarta.servlet.http.HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        if (id == null || file == null || file.isEmpty()) {
            result.put("success", false);
            result.put("message", "参数错误：缺少id或文件");
            return result;
        }
        try {
            String baseDir = System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "avatars";
            File dir = new File(baseDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String original = file.getOriginalFilename();
            String ext = "";
            if (original != null && original.lastIndexOf('.') != -1) {
                ext = original.substring(original.lastIndexOf('.'));
            }
            String filename = "psychologist_" + id + "_" + System.currentTimeMillis() + ext;
            File dest = new File(dir, filename);
            file.transferTo(dest);
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String url = baseUrl + "/uploads/avatars/" + filename;

            Psychologist update = new Psychologist();
            update.setId(id);
            update.setAvatar(url);
            boolean ok = psychologistService.updateById(update);

            result.put("success", ok);
            result.put("url", url);
            result.put("message", ok ? "头像更新成功" : "头像更新失败");
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "上传失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取专家收入趋势数据
     */
    @GetMapping("/stats/{id}/income-trend")
    public Map<String, Object> getIncomeTrend(@PathVariable Long id, 
                                               @RequestParam(defaultValue = "month") String range) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> trendData = consultationService.getIncomeTrend(id, range);
            result.put("success", true);
            result.put("data", trendData);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取收入趋势失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取专家咨询类型分布
     */
    @GetMapping("/stats/{id}/consultation-types")
    public Map<String, Object> getConsultationTypes(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> typeData = consultationService.getConsultationTypes(id);
            result.put("success", true);
            result.put("data", typeData);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取咨询类型分布失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取专家咨询时长统计
     */
    @GetMapping("/stats/{id}/duration-stats")
    public Map<String, Object> getDurationStats(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> durationData = consultationService.getDurationStats(id);
            result.put("success", true);
            result.put("data", durationData);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取时长统计失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取专家评分分布
     */
    @GetMapping("/stats/{id}/rating-distribution")
    public Map<String, Object> getRatingDistribution(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> ratingData = consultationService.getRatingDistribution(id);
            result.put("success", true);
            result.put("data", ratingData);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取评分分布失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取专家详细统计报表
     */
    @GetMapping("/stats/{id}/detailed")
    public Map<String, Object> getDetailedStats(@PathVariable Long id,
                                                 @RequestParam(required = false) String startDate,
                                                 @RequestParam(required = false) String endDate) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> detailedData = consultationService.getDetailedStats(id, startDate, endDate);
            result.put("success", true);
            result.put("data", detailedData);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取详细统计失败: " + e.getMessage());
        }
        return result;
    }
}