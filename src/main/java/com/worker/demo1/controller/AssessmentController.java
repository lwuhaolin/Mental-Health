package com.worker.demo1.controller;

import com.worker.demo1.dto.AssessmentQuestionDTO;
import com.worker.demo1.entity.Assessment;
import com.worker.demo1.entity.AssessmentQuestion;
import com.worker.demo1.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测评相关控制器
 * 处理测评问卷和题目的API请求
 */
@RestController
@RequestMapping("/assessments")
public class AssessmentController {
    
    @Autowired
    private AssessmentService assessmentService;
    
    /**
     * 获取所有测评问卷列表
     */
    @GetMapping("/list")
    public Map<String, Object> getAssessmentList() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Assessment> assessments = assessmentService.getAssessmentList();
            result.put("code", 200);
            result.put("message", "获取测评列表成功");
            result.put("data", assessments);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取测评列表失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 根据ID获取测评详情
     */
    @GetMapping("/{id}")
    public Map<String, Object> getAssessmentById(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            Assessment assessment = assessmentService.getAssessmentById(id);
            if (assessment != null) {
                result.put("code", 200);
                result.put("message", "获取测评详情成功");
                result.put("data", assessment);
            } else {
                result.put("code", 404);
                result.put("message", "测评不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取测评详情失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取测评题目列表
     */
    @GetMapping("/{assessmentId}/questions")
    public Map<String, Object> getAssessmentQuestions(@PathVariable Long assessmentId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<AssessmentQuestionDTO> questions = assessmentService.getAssessmentQuestionsDTO(assessmentId);
            result.put("code", 200);
            result.put("message", "获取题目列表成功");
            result.put("data", questions);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取题目列表失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 提交测评答案
     */
    @PostMapping("/{assessmentId}/submit")
    public Map<String, Object> submitAssessment(
            @PathVariable Long assessmentId,
            @RequestBody Map<String, Object> submission) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 从请求体中获取用户ID，如果没有则使用默认值
            Long userId = null;
            Object userIdObj = submission.get("userId");
            if (userIdObj != null) {
                if (userIdObj instanceof Integer) {
                    userId = ((Integer) userIdObj).longValue();
                } else if (userIdObj instanceof Long) {
                    userId = (Long) userIdObj;
                } else if (userIdObj instanceof String) {
                    userId = Long.parseLong((String) userIdObj);
                }
            }
            
            // 如果前端没有传userId，使用默认值
            if (userId == null) {
                userId = getCurrentUserId();
            }
            
            Map<String, Object> answers = (Map<String, Object>) submission.get("answers");
            Integer timeSpent = (Integer) submission.get("timeSpent");
            
            System.out.println("提交测评 - 用户ID: " + userId + ", 测评ID: " + assessmentId);
            System.out.println("答案数据: " + answers);
            System.out.println("答案数量: " + (answers != null ? answers.size() : 0));
            
            // 验证必要参数
            if (answers == null || answers.isEmpty()) {
                result.put("code", 400);
                result.put("message", "答案数据不能为空");
                return result;
            }
            
            // 保存测评结果
            Map<String, Object> assessmentResult = assessmentService.submitAssessment(
                assessmentId, userId, answers, timeSpent);
            
            result.put("code", 200);
            result.put("message", "测评提交成功");
            result.put("data", assessmentResult);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put("message", "测评提交失败: " + e.getMessage());
            result.put("error", e.getClass().getName());
        }
        return result;
    }
    
    /**
     * 获取测评结果（兼容前端路径参数版本）
     */
    @GetMapping("/result/{id}")
    public Map<String, Object> getAssessmentResultByPath(
            @PathVariable("id") Long assessmentId,
            @RequestParam("userId") Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, Object> assessmentResult = assessmentService.getAssessmentResult(assessmentId, userId);
            if (assessmentResult != null) {
                result.put("code", 200);
                result.put("message", "获取测评结果成功");
                result.put("data", assessmentResult);
            } else {
                result.put("code", 404);
                result.put("message", "测评结果不存在");
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取测评结果失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取用户的测评历史
     */
    @GetMapping("/user/{userId}/history")
    public Map<String, Object> getUserAssessmentHistory(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> history = assessmentService.getUserAssessmentHistory(userId);
            result.put("code", 200);
            result.put("message", "获取测评历史成功");
            result.put("data", history);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取测评历史失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 更新测评问卷的题目数量为真实数据
     */
    @PostMapping("/update-questions-count")
    public Map<String, Object> updateAssessmentQuestionsCount() {
        Map<String, Object> result = new HashMap<>();
        try {
            assessmentService.updateAssessmentQuestionsCount();
            result.put("code", 200);
            result.put("message", "成功更新测评问卷题目数量");
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "更新题目数量失败: " + e.getMessage());
        }
        return result;
    }
    
    /**
     * 模拟获取当前用户ID（实际项目中应从session或token中获取）
     */
    private Long getCurrentUserId() {
        // 这里返回一个模拟的用户ID，实际项目中需要实现用户认证
        return 4L; // 使用新创建的用户ID
    }
}