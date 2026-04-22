package com.worker.demo1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worker.demo1.dto.AssessmentQuestionDTO;
import com.worker.demo1.entity.Assessment;
import com.worker.demo1.entity.AssessmentQuestion;
import java.util.List;
import java.util.Map;

/**
 * 测评服务接口
 */
public interface AssessmentService extends IService<Assessment> {
    
    /**
     * 获取所有测评问卷列表
     */
    List<Assessment> getAssessmentList();
    
    /**
     * 根据ID获取测评详情
     */
    Assessment getAssessmentById(Long id);
    
    /**
     * 获取测评题目列表
     */
    List<AssessmentQuestion> getAssessmentQuestions(Long assessmentId);
    
    /**
     * 获取测评题目列表（转换为DTO格式）
     */
    List<AssessmentQuestionDTO> getAssessmentQuestionsDTO(Long assessmentId);
    
    /**
     * 提交测评答案
     */
    Map<String, Object> submitAssessment(Long assessmentId, Long userId, Map<String, Object> answers, Integer timeSpent);
    
    /**
     * 获取测评结果
     */
    Map<String, Object> getAssessmentResult(Long assessmentId, Long userId);
    
    /**
     * 获取用户的测评历史
     */
    List<Map<String, Object>> getUserAssessmentHistory(Long userId);
    
    /**
     * 更新测评问卷的题目数量为真实数据
     */
    void updateAssessmentQuestionsCount();
}