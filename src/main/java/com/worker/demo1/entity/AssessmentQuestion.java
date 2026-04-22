package com.worker.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 测评题目实体类
 * 对应数据库表：assessment_questions
 */
@Data
@TableName("assessment_questions")
public class AssessmentQuestion {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("assessment_id")
    private Long assessmentId;
    
    @TableField("question_number")
    private Integer questionNumber;
    
    @TableField("question_text")
    private String questionText;
    
    @TableField("question_type")
    private String questionType;
    
    @TableField("options")
    private String options; // JSON格式存储选项
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}