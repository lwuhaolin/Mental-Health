package com.worker.demo1.controller;

import com.worker.demo1.entity.Consultation;
import com.worker.demo1.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 咨询控制器
 */
@RestController
@RequestMapping("/consultation")
public class ConsultationController {
    
    @Autowired
    private ConsultationService consultationService;
    
    /**
     * 预约咨询
     */
    @PostMapping("/book")
    public Map<String, Object> bookConsultation(@RequestBody Consultation consultation) {
        Map<String, Object> result = new HashMap<>();
        boolean success = consultationService.bookConsultation(consultation);
        if (success) {
            result.put("success", true);
            result.put("message", "预约成功");
        } else {
            result.put("success", false);
            result.put("message", "预约失败");
        }
        return result;
    }
    
    /**
     * 专家接单
     */
    @PostMapping("/accept")
    public Map<String, Object> acceptConsultation(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        Long consultationId = Long.valueOf(data.get("consultationId").toString());
        Long psychologistId = Long.valueOf(data.get("psychologistId").toString());
        
        boolean success = consultationService.acceptConsultation(consultationId, psychologistId);
        if (success) {
            result.put("success", true);
            result.put("message", "接单成功");
        } else {
            result.put("success", false);
            result.put("message", "接单失败");
        }
        return result;
    }
    
    /**
     * 获取用户咨询记录（包含专家信息）
     */
    @GetMapping("/user/{userId}")
    public Map<String, Object> getUserConsultations(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> consultations = consultationService.getUserConsultationsWithPsychologist(userId);
        result.put("success", true);
        result.put("consultations", consultations);
        return result;
    }
    
    /**
     * 获取专家咨询记录
     */
    @GetMapping("/psychologist/{psychologistId}")
    public Map<String, Object> getPsychologistConsultations(@PathVariable Long psychologistId) {
        Map<String, Object> result = new HashMap<>();
        List<Consultation> consultations = consultationService.getPsychologistConsultations(psychologistId);
        result.put("success", true);
        result.put("consultations", consultations);
        return result;
    }
    
    /**
     * 完成咨询
     */
    @PostMapping("/complete")
    public Map<String, Object> completeConsultation(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long consultationId = Long.valueOf(data.get("consultationId").toString());
            Integer duration = data.get("duration") != null ? Integer.valueOf(data.get("duration").toString()) : 0;
            Integer rating = data.get("rating") != null ? Integer.valueOf(data.get("rating").toString()) : 0;
            String feedback = data.get("feedback") != null ? data.get("feedback").toString() : "";
            
            System.out.println("完成咨询请求 - 咨询ID: " + consultationId + 
                             ", 时长: " + duration + 
                             ", 评分: " + rating + 
                             ", 反馈: " + feedback);
            
            boolean success = consultationService.completeConsultation(consultationId, duration, rating, feedback);
            if (success) {
                result.put("success", true);
                result.put("message", "咨询完成");
            } else {
                result.put("success", false);
                result.put("message", "操作失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "完成咨询失败: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
    
    /**
     * 获取专家的患者列表
     */
    @GetMapping("/psychologist/{psychologistId}/patients")
    public Map<String, Object> getPsychologistPatients(@PathVariable Long psychologistId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> patients = consultationService.getPsychologistPatients(psychologistId);
            result.put("success", true);
            result.put("patients", patients);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取患者列表失败");
        }
        return result;
    }

    /**
     * 获取咨询详情
     */
    @GetMapping("/detail/{id}")
    public Map<String, Object> getConsultationDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Consultation consultation = consultationService.getById(id);
            if (consultation != null) {
                result.put("success", true);
                result.put("consultation", consultation);
            } else {
                result.put("success", false);
                result.put("message", "咨询记录不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取咨询详情失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 更新咨询状态
     */
    @PostMapping("/update-status")
    public Map<String, Object> updateConsultationStatus(@RequestParam("consultationId") Long consultationId, 
                                                       @RequestParam("status") String status) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 手动处理中文编码问题
            String processedStatus = status;
            if (status != null && status.contains("?")) {
                try {
                    processedStatus = new String(status.getBytes("ISO-8859-1"), "UTF-8");
                    System.out.println("编码转换前: " + status + ", 转换后: " + processedStatus);
                } catch (Exception e) {
                    System.out.println("编码转换失败: " + e.getMessage());
                }
            }
            
            System.out.println("接收到状态更新请求 - 咨询ID: " + consultationId + ", 状态: " + processedStatus);
            
            boolean success = consultationService.updateConsultationStatus(consultationId, processedStatus);
            if (success) {
                result.put("success", true);
                result.put("message", "状态更新成功");
            } else {
                result.put("success", false);
                result.put("message", "状态更新失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新状态失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 提交咨询反馈
     */
    @PostMapping("/{consultationId}/feedback")
    public Map<String, Object> submitFeedback(@PathVariable Long consultationId, 
                                              @RequestBody Map<String, Object> feedbackData) {
        Map<String, Object> result = new HashMap<>();
        try {
            Integer rating = Integer.valueOf(feedbackData.get("userRating").toString());
            String feedback = feedbackData.get("userFeedback").toString();
            
            System.out.println("接收到反馈提交请求 - 咨询ID: " + consultationId + ", 评分: " + rating + ", 反馈: " + feedback);
            
            boolean success = consultationService.submitFeedback(consultationId, rating, feedback);
            if (success) {
                result.put("success", true);
                result.put("message", "反馈提交成功");
            } else {
                result.put("success", false);
                result.put("message", "反馈提交失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "提交反馈失败: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }
}