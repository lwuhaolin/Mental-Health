package com.worker.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户测评记录实体类
 * 对应数据库表：user_assessments
 */
@Data
@TableName("user_assessments")
public class UserAssessment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("assessment_id")
    private Long assessmentId;
    
    @TableField("score")
    private BigDecimal score;
    
    @TableField("result_level")
    private String resultLevel;
    
    @TableField("result_description")
    private String resultDescription;
    
    @TableField("suggestions")
    private String suggestions;
    
    @TableField("start_time")
    private LocalDateTime startTime;
    
    @TableField("end_time")
    private LocalDateTime endTime;
    
    @TableField("status")
    private String status;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}