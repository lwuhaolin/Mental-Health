package com.worker.demo1.controller;

import com.worker.demo1.entity.ConsultationAppointment;
import com.worker.demo1.entity.Consultation;
import com.worker.demo1.service.ConsultationAppointmentService;
import com.worker.demo1.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * 咨询预约控制器
 */
@RestController
@RequestMapping("/consultation")
public class ConsultationAppointmentController {
    
    @Autowired
    private ConsultationAppointmentService appointmentService;
    
    @Autowired
    private ConsultationService consultationService;
    
    /**
     * 创建咨询预约
     */
    @PostMapping("/appointment")
    public Map<String, Object> createAppointment(@RequestBody Map<String, Object> appointmentData) {
        Map<String, Object> result = new HashMap<>();
        try {
            ConsultationAppointment appointment = new ConsultationAppointment();
            appointment.setUserId(Long.valueOf(appointmentData.get("userId").toString()));
            appointment.setPsychologistId(Long.valueOf(appointmentData.get("psychologistId").toString()));
            appointment.setTopic(appointmentData.get("topic").toString());
            appointment.setContactInfo(appointmentData.get("contactInfo").toString());
            
            // 处理日期时间格式 - 支持 ISO 格式和自定义格式
            String preferredTimeStr = appointmentData.get("preferredTime").toString();
            java.time.LocalDateTime preferredTime;
            
            try {
                // 首先尝试解析 ISO 格式
                preferredTime = java.time.LocalDateTime.parse(preferredTimeStr);
            } catch (Exception e1) {
                try {
                    // 如果 ISO 格式失败，尝试自定义格式
                    preferredTime = java.time.LocalDateTime.parse(preferredTimeStr, 
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } catch (Exception e2) {
                    throw new RuntimeException("时间格式错误，期望格式: yyyy-MM-dd HH:mm:ss 或 ISO 格式");
                }
            }
            appointment.setPreferredTime(preferredTime);
            
            boolean success = appointmentService.createAppointment(appointment);
            if (success) {
                result.put("success", true);
                result.put("message", "预约提交成功");
                result.put("appointmentId", appointment.getId());
            } else {
                result.put("success", false);
                result.put("message", "预约提交失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据用户ID获取预约列表
     */
    @GetMapping("/appointments/user/{userId}")
    public Map<String, Object> getUserAppointments(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            var appointments = appointmentService.getAppointmentsByUserId(userId);
            result.put("success", true);
            result.put("appointments", appointments);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取预约列表失败");
        }
        return result;
    }
    
    /**
     * 根据专家ID获取预约列表
     */
    @GetMapping("/appointments/psychologist/{psychologistId}")
    public Map<String, Object> getPsychologistAppointments(@PathVariable Long psychologistId) {
        Map<String, Object> result = new HashMap<>();
        try {
            var appointments = appointmentService.getAppointmentsByPsychologistId(psychologistId);
            result.put("success", true);
            result.put("appointments", appointments);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取预约列表失败");
        }
        return result;
    }
    
    /**
     * 专家接单
     */
    @PostMapping("/appointment/accept")
    public Map<String, Object> acceptAppointment(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long appointmentId = Long.valueOf(data.get("appointmentId").toString());
            
            // 获取预约信息
            ConsultationAppointment appointment = appointmentService.getById(appointmentId);
            if (appointment == null) {
                result.put("success", false);
                result.put("message", "预约记录不存在");
                return result;
            }
            
            // 查找对应的咨询记录（基于用户ID、专家ID和主题匹配）
            List<Consultation> consultations = consultationService.getConsultationsByUserId(appointment.getUserId());
            Consultation targetConsultation = null;
            
            for (Consultation consultation : consultations) {
                if (consultation.getPsychologistId().equals(appointment.getPsychologistId()) &&
                    consultation.getTitle().equals(appointment.getTopic()) &&
                    "待接单".equals(consultation.getStatus())) {
                    targetConsultation = consultation;
                    break;
                }
            }
            
            if (targetConsultation == null) {
                result.put("success", false);
                result.put("message", "未找到对应的咨询记录");
                return result;
            }
            
            // 更新预约记录状态为"confirmed"
            boolean appointmentSuccess = appointmentService.updateAppointmentStatus(appointmentId, "confirmed");
            
            // 更新咨询记录状态为"进行中"，并设置开始时间
            targetConsultation.setStatus("进行中");
            targetConsultation.setStartTime(java.time.LocalDateTime.now());
            targetConsultation.setUpdateTime(java.time.LocalDateTime.now());
            boolean consultationSuccess = consultationService.updateById(targetConsultation);
            
            if (appointmentSuccess && consultationSuccess) {
                result.put("success", true);
                result.put("message", "接单成功");
                result.put("consultationId", targetConsultation.getId());
            } else {
                result.put("success", false);
                result.put("message", "接单失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 取消预约
     */
    @PostMapping("/appointment/cancel")
    public Map<String, Object> cancelAppointment(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long appointmentId = Long.valueOf(data.get("appointmentId").toString());
            boolean success = appointmentService.updateAppointmentStatus(appointmentId, "cancelled");
            if (success) {
                result.put("success", true);
                result.put("message", "预约已取消");
            } else {
                result.put("success", false);
                result.put("message", "取消失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "系统错误：" + e.getMessage());
        }
        return result;
    }
}