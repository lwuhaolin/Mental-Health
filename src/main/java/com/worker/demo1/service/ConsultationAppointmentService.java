package com.worker.demo1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worker.demo1.entity.ConsultationAppointment;
import java.util.List;

/**
 * 咨询预约服务接口
 */
public interface ConsultationAppointmentService extends IService<ConsultationAppointment> {
    
    /**
     * 创建咨询预约
     */
    boolean createAppointment(ConsultationAppointment appointment);
    
    /**
     * 根据用户ID获取预约列表
     */
    List<ConsultationAppointment> getAppointmentsByUserId(Long userId);
    
    /**
     * 根据专家ID获取预约列表
     */
    List<ConsultationAppointment> getAppointmentsByPsychologistId(Long psychologistId);
    
    /**
     * 更新预约状态
     */
    boolean updateAppointmentStatus(Long appointmentId, String status);
}