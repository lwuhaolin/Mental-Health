package com.worker.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 咨询预约实体类
 */
@Data
@TableName("consultation_appointments")
public class ConsultationAppointment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;
    
    private Long psychologistId;
    
    private String topic;
    
    private LocalDateTime preferredTime;
    
    private String contactInfo;
    
    private String status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    // 关联的专家信息（非数据库字段）
    @TableField(exist = false)
    private Psychologist psychologist;
    
    // 关联的用户信息（非数据库字段）
    @TableField(exist = false)
    private User user;
}