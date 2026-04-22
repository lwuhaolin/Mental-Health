package com.worker.demo1.controller;

import com.worker.demo1.entity.Admin;
import com.worker.demo1.entity.User;
import com.worker.demo1.entity.Psychologist;
import com.worker.demo1.entity.Assessment;
import com.worker.demo1.entity.Consultation;
import com.worker.demo1.entity.MessageBottle;
import com.worker.demo1.entity.AiInteraction;
import com.worker.demo1.service.AdminService;
import com.worker.demo1.service.UserService;
import com.worker.demo1.service.PsychologistService;
import com.worker.demo1.service.AssessmentService;
import com.worker.demo1.service.ConsultationService;
import com.worker.demo1.service.MessageBottleService;
import com.worker.demo1.service.AiInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PsychologistService psychologistService;
    
    @Autowired
    private AssessmentService assessmentService;
    
    @Autowired
    private ConsultationService consultationService;
    
    @Autowired
    private MessageBottleService messageBottleService;
    
    @Autowired
    private AiInteractionService aiInteractionService;
    
    /**
     * 管理员登录
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> loginData) {
        Map<String, Object> result = new HashMap<>();
        String username = loginData.get("username");
        String password = loginData.get("password");
        
        Admin admin = adminService.login(username, password);
        if (admin != null) {
            result.put("success", true);
            result.put("message", "登录成功");
            result.put("admin", admin);
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }
        return result;
    }
    
    /**
     * 获取管理员列表
     */
    @GetMapping("/list")
    public Map<String, Object> getAdminList() {
        Map<String, Object> result = new HashMap<>();
        List<Admin> admins = adminService.list();
        result.put("success", true);
        result.put("admins", admins);
        return result;
    }
    
    /**
     * 获取系统统计数据
     */
    @GetMapping("/stats")
    public Map<String, Object> getSystemStats() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取今天的开始时间
            java.time.LocalDateTime todayStart = java.time.LocalDate.now().atStartOfDay();
            
            // 从数据库获取真实统计数据
            long totalUsers = userService.count();
            long totalPsychologists = psychologistService.count();
            
            // 由于AssessmentService和ConsultationService没有count方法，使用其他方式获取
            List<Assessment> allAssessments = assessmentService.getAssessmentList();
            long totalAssessments = allAssessments.size();
            
            // 计算测评总次数（所有测评的test_count之和）
            long totalAssessmentTests = allAssessments.stream()
                .mapToLong(a -> a.getTestCount() != null ? a.getTestCount() : 0)
                .sum();
            
            long totalConsultations = consultationService.list().size();
            
            // 获取活跃用户数（最近30天登录的用户）
            long activeUsers = userService.getActiveUserCount();
            
            // 统计今日新增数据
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<User> userWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            userWrapper.ge("create_time", todayStart);
            long todayNewUsers = userService.count(userWrapper);
            
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Psychologist> psyWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            psyWrapper.ge("create_time", todayStart);
            long todayNewPsychologists = psychologistService.count(psyWrapper);
            
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Consultation> consultWrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            consultWrapper.ge("create_time", todayStart);
            long todayNewConsultations = consultationService.count(consultWrapper);
            
            result.put("success", true);
            result.put("totalUsers", totalUsers);
            result.put("totalPsychologists", totalPsychologists);
            result.put("totalAssessments", totalAssessments);
            result.put("totalAssessmentTests", totalAssessmentTests);
            result.put("totalConsultations", totalConsultations);
            result.put("activeUsers", activeUsers);
            result.put("todayNewUsers", todayNewUsers);
            result.put("todayNewPsychologists", todayNewPsychologists);
            result.put("todayNewConsultations", todayNewConsultations);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取统计数据失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取用户管理数据
     */
    @GetMapping("/user-management")
    public Map<String, Object> getUserManagementData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从数据库获取真实用户数据
            List<User> users = userService.list();
            result.put("success", true);
            result.put("users", users);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取用户数据失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取专家管理数据
     */
    @GetMapping("/psychologist-management")
    public Map<String, Object> getPsychologistManagementData() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 从数据库获取真实专家数据
            List<Psychologist> psychologists = psychologistService.list();
            result.put("success", true);
            result.put("psychologists", psychologists);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取专家数据失败: " + e.getMessage());
        }
        
        return result;
    }

    // 用户CRUD
    @PostMapping("/user")
    public Map<String, Object> createUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 兼容前端未提供必填字段的情况，设置合理默认值
            if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
                String base = null;
                if (user.getEmail() != null && !user.getEmail().trim().isEmpty() && user.getEmail().contains("@")) {
                    base = user.getEmail().substring(0, user.getEmail().indexOf('@'));
                } else if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
                    base = user.getRealName().trim();
                } else {
                    base = "user";
                }
                user.setUsername(base + "_" + System.currentTimeMillis());
            }
            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                user.setPassword("123456");
            }
            if (user.getStatus() == null || user.getStatus().trim().isEmpty()) {
                user.setStatus("1");
            } else {
                String s = user.getStatus().trim();
                if (s.matches("^\\d+$")) {
                    // 已是数字码值，直接使用
                    user.setStatus(s);
                } else if ("正常".equals(s) || s.contains("正") || "active".equalsIgnoreCase(s)) {
                    user.setStatus("1");
                } else if ("禁用".equals(s) || s.contains("禁") || "inactive".equalsIgnoreCase(s)) {
                    user.setStatus("0");
                } else {
                    // 兜底：非预期字符串一律按启用处理
                    user.setStatus("1");
                }
            }

            boolean success = userService.save(user);
            result.put("success", success);
            result.put("message", success ? "创建用户成功" : "创建用户失败");
            result.put("data", user);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建用户失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/user")
    public Map<String, Object> updateUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 将前端传入的中文/英文状态统一映射为数据库码值（更健壮）
            if (user.getStatus() != null) {
                String s = user.getStatus().trim();
                if (s.matches("^\\d+$")) {
                    user.setStatus(s);
                } else if ("正常".equals(s) || s.contains("正") || "active".equalsIgnoreCase(s)) {
                    user.setStatus("1");
                } else if ("禁用".equals(s) || s.contains("禁") || "inactive".equalsIgnoreCase(s)) {
                    user.setStatus("0");
                } else {
                    user.setStatus("1");
                }
            }
            boolean success = userService.updateById(user);
            result.put("success", success);
            result.put("message", success ? "更新用户成功" : "更新用户失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新用户失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/user/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = userService.removeById(id);
            result.put("success", success);
            result.put("message", success ? "删除用户成功" : "删除用户失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除用户失败: " + e.getMessage());
        }
        return result;
    }

    // 专家CRUD
    @PostMapping("/psychologist")
    public Map<String, Object> createPsychologist(@RequestBody Psychologist psychologist) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 设置默认字段，避免前端未提供必填项导致失败
            if (psychologist.getUsername() == null || psychologist.getUsername().trim().isEmpty()) {
                String base = null;
                if (psychologist.getEmail() != null && !psychologist.getEmail().trim().isEmpty() && psychologist.getEmail().contains("@")) {
                    base = psychologist.getEmail().substring(0, psychologist.getEmail().indexOf('@'));
                } else if (psychologist.getRealName() != null && !psychologist.getRealName().trim().isEmpty()) {
                    base = psychologist.getRealName().trim();
                } else {
                    base = "psy";
                }
                psychologist.setUsername(base + "_" + System.currentTimeMillis());
            }
            if (psychologist.getPassword() == null || psychologist.getPassword().trim().isEmpty()) {
                psychologist.setPassword("123456");
            }
            if (psychologist.getStatus() == null || psychologist.getStatus().trim().isEmpty()) {
                psychologist.setStatus("正常");
            }
            if (psychologist.getRating() == null) {
                psychologist.setRating(java.math.BigDecimal.ZERO);
            }
            if (psychologist.getAccount() == null) {
                psychologist.setAccount(java.math.BigDecimal.ZERO);
            }

            boolean success = psychologistService.save(psychologist);
            result.put("success", success);
            result.put("message", success ? "创建专家成功" : "创建专家失败");
            result.put("data", psychologist);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建专家失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/psychologist")
    public Map<String, Object> updatePsychologist(@RequestBody Psychologist psychologist) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = psychologistService.updateById(psychologist);
            result.put("success", success);
            result.put("message", success ? "更新专家成功" : "更新专家失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新专家失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/psychologist/{id}")
    public Map<String, Object> deletePsychologist(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = psychologistService.removeById(id);
            result.put("success", success);
            result.put("message", success ? "删除专家成功" : "删除专家失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除专家失败: " + e.getMessage());
        }
        return result;
    }

    // 测评管理
    @GetMapping("/assessment-management")
    public Map<String, Object> getAssessmentManagementData() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Assessment> assessments = assessmentService.getAssessmentList();
            result.put("success", true);
            result.put("assessments", assessments);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取测评数据失败: " + e.getMessage());
        }
        return result;
    }

    @PostMapping("/assessment")
    public Map<String, Object> createAssessment(@RequestBody Assessment assessment) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = assessmentService.save(assessment);
            result.put("success", success);
            result.put("message", success ? "创建测评成功" : "创建测评失败");
            result.put("data", assessment);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建测评失败: " + e.getMessage());
        }
        return result;
    }

    @PutMapping("/assessment")
    public Map<String, Object> updateAssessment(@RequestBody Assessment assessment) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = assessmentService.updateById(assessment);
            result.put("success", success);
            result.put("message", success ? "更新测评成功" : "更新测评失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新测评失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/assessment/{id}")
    public Map<String, Object> deleteAssessment(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = assessmentService.removeById(id);
            result.put("success", success);
            result.put("message", success ? "删除测评成功" : "删除测评失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除测评失败: " + e.getMessage());
        }
        return result;
    }

    // 咨询记录管理
    @GetMapping("/consultation-management")
    public Map<String, Object> getConsultationManagementData() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Consultation> consultations = consultationService.list();
            // 关联查询用户和专家信息
            List<Map<String, Object>> consultationList = new ArrayList<>();
            for (Consultation consultation : consultations) {
                Map<String, Object> consultationMap = new HashMap<>();
                consultationMap.put("id", consultation.getId());
                consultationMap.put("title", consultation.getTitle());
                consultationMap.put("description", consultation.getDescription());
                consultationMap.put("consultationType", consultation.getConsultationType());
                consultationMap.put("status", consultation.getStatus());
                consultationMap.put("scheduledTime", consultation.getScheduledTime());
                consultationMap.put("cost", consultation.getCost());
                consultationMap.put("userRating", consultation.getUserRating());
                consultationMap.put("userFeedback", consultation.getUserFeedback());
                
                // 查询用户信息
                if (consultation.getUserId() != null) {
                    User user = userService.getById(consultation.getUserId());
                    consultationMap.put("userName", user != null ? user.getRealName() : "未知用户");
                } else {
                    consultationMap.put("userName", "未知用户");
                }
                
                // 查询专家信息
                if (consultation.getPsychologistId() != null) {
                    Psychologist psychologist = psychologistService.getById(consultation.getPsychologistId());
                    consultationMap.put("psychologistName", psychologist != null ? psychologist.getRealName() : "未知专家");
                } else {
                    consultationMap.put("psychologistName", "未知专家");
                }
                
                consultationList.add(consultationMap);
            }
            
            result.put("success", true);
            result.put("consultations", consultationList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取咨询记录失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/consultation/{id}")
    public Map<String, Object> deleteConsultation(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = consultationService.removeById(id);
            result.put("success", success);
            result.put("message", success ? "删除咨询记录成功" : "删除咨询记录失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除咨询记录失败: " + e.getMessage());
        }
        return result;
    }

    // 漂流瓶管理
    @GetMapping("/bottle-management")
    public Map<String, Object> getBottleManagementData() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<MessageBottle> bottles = messageBottleService.list();
            // 关联查询用户信息
            List<Map<String, Object>> bottleList = new ArrayList<>();
            for (MessageBottle bottle : bottles) {
                Map<String, Object> bottleMap = new HashMap<>();
                bottleMap.put("id", bottle.getId());
                bottleMap.put("content", bottle.getContent());
                bottleMap.put("likes", bottle.getLikeCount());
                bottleMap.put("replyCount", bottle.getReplyCount());
                bottleMap.put("createTime", bottle.getCreateTime());
                
                // 查询用户信息
                if (bottle.getUserId() != null) {
                    User user = userService.getById(bottle.getUserId());
                    bottleMap.put("userName", user != null ? user.getRealName() : "匿名");
                } else {
                    bottleMap.put("userName", "匿名");
                }
                
                bottleList.add(bottleMap);
            }
            
            result.put("success", true);
            result.put("bottles", bottleList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取漂流瓶数据失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/bottle/{id}")
    public Map<String, Object> deleteBottle(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageBottleService.removeById(id);
            result.put("success", success);
            result.put("message", success ? "删除漂流瓶成功" : "删除漂流瓶失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除漂流瓶失败: " + e.getMessage());
        }
        return result;
    }

    // AI对话记录管理
    @GetMapping("/ai-management")
    public Map<String, Object> getAIManagementData() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<AiInteraction> aiRecords = aiInteractionService.list();
            // 关联查询用户信息
            List<Map<String, Object>> aiRecordList = new ArrayList<>();
            for (AiInteraction aiRecord : aiRecords) {
                Map<String, Object> aiRecordMap = new HashMap<>();
                aiRecordMap.put("id", aiRecord.getId());
                aiRecordMap.put("sessionType", aiRecord.getInteractionType());
                aiRecordMap.put("userMessage", aiRecord.getUserMessage());
                aiRecordMap.put("aiResponse", aiRecord.getAiResponse());
                aiRecordMap.put("createTime", aiRecord.getCreateTime());
                
                // 查询用户信息
                if (aiRecord.getUserId() != null) {
                    User user = userService.getById(aiRecord.getUserId());
                    aiRecordMap.put("userName", user != null ? user.getRealName() : "未知用户");
                } else {
                    aiRecordMap.put("userName", "未知用户");
                }
                
                aiRecordList.add(aiRecordMap);
            }
            
            result.put("success", true);
            result.put("aiRecords", aiRecordList);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取AI对话记录失败: " + e.getMessage());
        }
        return result;
    }

    @DeleteMapping("/ai/{id}")
    public Map<String, Object> deleteAIRecord(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = aiInteractionService.removeById(id);
            result.put("success", success);
            result.put("message", success ? "删除AI对话记录成功" : "删除AI对话记录失败");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除AI对话记录失败: " + e.getMessage());
        }
        return result;
    }
}