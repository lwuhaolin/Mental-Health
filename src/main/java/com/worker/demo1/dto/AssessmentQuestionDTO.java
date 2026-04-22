package com.worker.demo1.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 测评题目数据传输对象
 * 用于前端展示，包含转换后的选项列表
 */
@Data
public class AssessmentQuestionDTO {
    private Long id;
    private Long assessmentId;
    private Integer questionNumber;
    private String questionText;
    private String questionType;
    private List<String> options; // 转换后的选项列表
    private LocalDateTime createTime;
}