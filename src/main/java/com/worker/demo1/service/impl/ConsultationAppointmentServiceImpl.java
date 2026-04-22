package com.worker.demo1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worker.demo1.entity.ConsultationAppointment;
import com.worker.demo1.mapper.ConsultationAppointmentMapper;
import com.worker.demo1.service.ConsultationAppointmentService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 咨询预约服务实现类
 */
@Service
public class ConsultationAppointmentServiceImpl 
    extends ServiceImpl<ConsultationAppointmentMapper, ConsultationAppointment> 
    implements ConsultationAppointmentService {
    
    @Override
    public boolean createAppointment(ConsultationAppointment appointment) {
        // 设置默认状态
        if (appointment.getStatus() == null) {
            appointment.setStatus("pending");
        }
        return save(appointment);
    }
    
    @Override
    public List<ConsultationAppointment> getAppointmentsByUserId(Long userId) {
        return lambdaQuery()
                .eq(ConsultationAppointment::getUserId, userId)
                .orderByDesc(ConsultationAppointment::getCreateTime)
                .list();
    }
    
    @Override
    public List<ConsultationAppointment> getAppointmentsByPsychologistId(Long psychologistId) {
        return lambdaQuery()
                .eq(ConsultationAppointment::getPsychologistId, psychologistId)
                .orderByDesc(ConsultationAppointment::getCreateTime)
                .list();
    }
    
    @Override
    public boolean updateAppointmentStatus(Long appointmentId, String status) {
        ConsultationAppointment appointment = getById(appointmentId);
        if (appointment != null) {
            appointment.setStatus(status);
            return updateById(appointment);
        }
        return false;
    }
}