package com.worker.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 心理测评问卷实体类
 * 对应数据库表：assessments
 */
@Data
@TableName("assessments")
public class Assessment {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("title")
    private String title;
    
    @TableField("description")
    private String description;
    
    @TableField("category")
    private String category;
    
    @TableField("questions_count")
    private Integer questionsCount;
    
    @TableField("estimated_time")
    private Integer estimatedTime;
    
    @TableField("status")
    private String status;
    
    @TableField("test_count")
    private Integer testCount;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}