package com.worker.demo1.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 测评答案实体类
 */
@Data
@TableName("assessment_answers")
public class AssessmentAnswer {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userAssessmentId;
    
    private Long questionId;
    
    private String answer;
    
    private Integer score;
    
    private LocalDateTime createTime;
}