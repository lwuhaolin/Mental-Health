package com.worker.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.worker.demo1.dto.AssessmentQuestionDTO;
import com.worker.demo1.entity.Assessment;
import com.worker.demo1.entity.AssessmentQuestion;
import com.worker.demo1.entity.UserAssessment;
import com.worker.demo1.entity.AssessmentAnswer;
import com.worker.demo1.mapper.AssessmentMapper;
import com.worker.demo1.mapper.AssessmentQuestionMapper;
import com.worker.demo1.mapper.UserAssessmentMapper;
import com.worker.demo1.mapper.AssessmentAnswerMapper;
import com.worker.demo1.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 测评服务实现类
 */
@Service
public class AssessmentServiceImpl extends ServiceImpl<AssessmentMapper, Assessment> implements AssessmentService {
    
    @Autowired
    private AssessmentMapper assessmentMapper;
    
    @Autowired
    private AssessmentQuestionMapper questionMapper;
    
    @Autowired
    private UserAssessmentMapper userAssessmentMapper;
    
    @Autowired
    private AssessmentAnswerMapper assessmentAnswerMapper;
    
    @Override
    public List<Assessment> getAssessmentList() {
        QueryWrapper<Assessment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "启用")
                   .orderByDesc("create_time");
        
        List<Assessment> assessments = assessmentMapper.selectList(queryWrapper);
        
        // 为每个测评动态计算题目数量并更新数据库
        for (Assessment assessment : assessments) {
            QueryWrapper<AssessmentQuestion> questionQueryWrapper = new QueryWrapper<>();
            questionQueryWrapper.eq("assessment_id", assessment.getId());
            Long questionCount = questionMapper.selectCount(questionQueryWrapper);
            
            // 如果题目数量发生变化，更新数据库
            if (assessment.getQuestionsCount() == null || 
                !assessment.getQuestionsCount().equals(questionCount.intValue())) {
                assessment.setQuestionsCount(questionCount.intValue());
                assessmentMapper.updateById(assessment);
            }
        }
        
        return assessments;
    }
    
    @Override
    public Assessment getAssessmentById(Long id) {
        return assessmentMapper.selectById(id);
    }
    
    @Override
    public List<AssessmentQuestion> getAssessmentQuestions(Long assessmentId) {
        QueryWrapper<AssessmentQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("assessment_id", assessmentId)
                   .orderByAsc("question_number");
        return questionMapper.selectList(queryWrapper);
    }
    
    /**
     * 获取测评题目列表（转换为DTO格式）
     */
    public List<AssessmentQuestionDTO> getAssessmentQuestionsDTO(Long assessmentId) {
        List<AssessmentQuestion> questions = getAssessmentQuestions(assessmentId);
        List<AssessmentQuestionDTO> dtoList = new ArrayList<>();
        
        ObjectMapper objectMapper = new ObjectMapper();
        for (AssessmentQuestion question : questions) {
            AssessmentQuestionDTO dto = new AssessmentQuestionDTO();
            dto.setId(question.getId());
            dto.setAssessmentId(question.getAssessmentId());
            dto.setQuestionNumber(question.getQuestionNumber());
            dto.setQuestionText(question.getQuestionText());
            dto.setQuestionType(question.getQuestionType());
            dto.setCreateTime(question.getCreateTime());
            
            // 处理options字段的JSON转换
            try {
                String optionsJson = question.getOptions();
                if (optionsJson != null && !optionsJson.isEmpty()) {
                    List<String> optionsList = objectMapper.readValue(optionsJson, new TypeReference<List<String>>() {});
                    dto.setOptions(optionsList);
                } else {
                    dto.setOptions(new ArrayList<>());
                }
            } catch (Exception e) {
                dto.setOptions(new ArrayList<>());
            }
            
            dtoList.add(dto);
        }
        
        return dtoList;
    }
    
    @Override
    public Map<String, Object> submitAssessment(Long assessmentId, Long userId, Map<String, Object> answers, Integer timeSpent) {
        System.out.println("========== 开始提交测评 ==========");
        System.out.println("测评ID: " + assessmentId);
        System.out.println("用户ID: " + userId);
        System.out.println("答案数量: " + (answers != null ? answers.size() : 0));
        System.out.println("耗时: " + timeSpent + "秒");
        
        // 获取测评题目
        List<AssessmentQuestion> questions = questionMapper.selectList(
            new QueryWrapper<AssessmentQuestion>()
                .eq("assessment_id", assessmentId)
                .orderByAsc("question_number")
        );
        
        System.out.println("查询到题目数量: " + questions.size());
        
        if (questions.isEmpty()) {
            throw new RuntimeException("该测评没有题目数据");
        }
        
        int totalScore = 0;
        int answeredCount = 0;
        int maxPossibleScore = 0;
        
        // 计算总分 - 根据题目类型应用不同的评分规则
        for (AssessmentQuestion question : questions) {
            Object answer = answers.get(question.getId().toString());
            
            // 根据题目类型计算最高分
            String questionType = question.getQuestionType();
            System.out.println("题目" + question.getQuestionNumber() + " - ID: " + question.getId() + 
                             ", 类型: " + questionType + ", 答案: " + answer);
            
            int questionMaxScore = getMaxScoreForQuestionType(questionType);
            maxPossibleScore += questionMaxScore;
            
            if (answer != null) {
                answeredCount++;
                // 根据题目类型和选项索引计算分数
                int score = calculateQuestionScore(questionType, answer.toString());
                totalScore += score;
                System.out.println("  -> 得分: " + score);
            }
        }
        
        System.out.println("总分: " + totalScore + " / " + maxPossibleScore);
        System.out.println("完成题数: " + answeredCount + " / " + questions.size());
        
        // 计算完成率
        double completionRate = questions.size() > 0 ? (double) answeredCount / questions.size() * 100 : 0;
        
        // 根据总分和测评类型评估结果
        Assessment assessment = assessmentMapper.selectById(assessmentId);
        if (assessment == null) {
            throw new RuntimeException("测评不存在，ID: " + assessmentId);
        }
        
        String category = assessment.getCategory();
        System.out.println("测评类别: " + category);
        
        Map<String, String> result = evaluateAssessmentResult(totalScore, maxPossibleScore, category);
        String level = result.get("level");
        String suggestion = result.get("suggestion");
        
        System.out.println("评估等级: " + level);
        System.out.println("建议: " + suggestion);
        
        // 保存测评结果到数据库
        UserAssessment userAssessment = new UserAssessment();
        userAssessment.setUserId(userId);
        userAssessment.setAssessmentId(assessmentId);
        userAssessment.setScore(new java.math.BigDecimal(totalScore));
        userAssessment.setResultLevel(level);
        userAssessment.setSuggestions(suggestion);
        userAssessment.setStartTime(java.time.LocalDateTime.now());
        userAssessment.setEndTime(java.time.LocalDateTime.now());
        userAssessment.setStatus("已完成");
        
        System.out.println("准备插入user_assessments表...");
        try {
            int insertResult = userAssessmentMapper.insert(userAssessment);
            System.out.println("插入结果: " + insertResult + ", 生成的ID: " + userAssessment.getId());
        } catch (Exception e) {
            System.err.println("保存测评记录失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("保存测评记录失败: " + e.getMessage(), e);
        }
        
        // 保存每个题目的答案和得分
        System.out.println("准备保存答案，测评记录ID: " + userAssessment.getId());
        int savedAnswerCount = 0;
        try {
            
            for (AssessmentQuestion question : questions) {
                Object answer = answers.get(question.getId().toString());
                if (answer != null) {
                    AssessmentAnswer assessmentAnswer = new AssessmentAnswer();
                    assessmentAnswer.setUserAssessmentId(userAssessment.getId());
                    assessmentAnswer.setQuestionId(question.getId());
                    assessmentAnswer.setAnswer(answer.toString());
                    
                    // 计算单个题目的分数
                    String questionType = question.getQuestionType();
                    int questionScore = calculateQuestionScore(questionType, answer.toString());
                    assessmentAnswer.setScore(questionScore);
                    
                    assessmentAnswerMapper.insert(assessmentAnswer);
                    savedAnswerCount++;
                }
            }
            
            System.out.println("成功保存答案数量: " + savedAnswerCount);
        } catch (Exception e) {
            System.err.println("保存答案失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("保存答案失败: " + e.getMessage(), e);
        }
        
        // 更新测评的测试次数（test_count加1）
        try {
            Integer currentCount = assessment.getTestCount();
            if (currentCount == null) {
                currentCount = 0;
            }
            assessment.setTestCount(currentCount + 1);
            assessmentMapper.updateById(assessment);
            System.out.println("更新测试次数: " + assessment.getTestCount());
        } catch (Exception e) {
            System.err.println("更新测试次数失败: " + e.getMessage());
            // 这个错误不影响主流程，只记录日志
        }
        
        System.out.println("========== 测评提交成功 ==========");
        
        return Map.of(
            "id", userAssessment.getId(),
            "score", totalScore,
            "maxScore", maxPossibleScore,
            "answeredCount", answeredCount,
            "totalQuestions", questions.size(),
            "completionRate", String.format("%.1f%%", completionRate),
            "level", level,
            "suggestion", suggestion,
            "timeSpent", timeSpent != null ? timeSpent : 0,
            "category", category
        );
    }
    
    /**
     * 根据题目类型获取最高分
     */
    private int getMaxScoreForQuestionType(String questionType) {
        if (questionType == null) {
            return 1;
        }
        
        // 支持多种格式：中文、英文、数据库格式
        String type = questionType.toLowerCase().trim();
        
        if (type.contains("量表") || type.equals("scale")) {
            return 3; // 量表题最高3分（0-3）
        } else if (type.contains("单选") || type.equals("single") || type.equals("single_choice")) {
            return 1; // 单选题1分
        } else if (type.contains("多选") || type.equals("multiple") || type.equals("multiple_choice")) {
            return 2; // 多选题2分
        }
        return 1; // 默认1分
    }
    
    /**
     * 根据题目类型和答案计算分数
     */
    private int calculateQuestionScore(String questionType, String answer) {
        try {
            if (questionType == null || answer == null) {
                return 0;
            }
            
            // 支持多种格式：中文、英文、数据库格式
            String type = questionType.toLowerCase().trim();
            
            if (type.contains("量表") || type.equals("scale")) {
                // 量表题：选项索引就是分数（0-3或0-4）
                int optionIndex = Integer.parseInt(answer);
                return Math.max(0, Math.min(4, optionIndex)); // 确保在0-4范围内
            } else if (type.contains("单选") || type.equals("single") || type.equals("single_choice")) {
                // 单选题：回答了就得1分
                return 1;
            } else if (type.contains("多选") || type.equals("multiple") || type.equals("multiple_choice")) {
                // 多选题：根据选择数量计分
                String[] selections = answer.split(",");
                return Math.min(2, selections.length); // 最多2分
            }
        } catch (Exception e) {
            System.err.println("计算题目分数失败 - 题目类型: " + questionType + ", 答案: " + answer + ", 错误: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * 根据总分和测评类型评估结果
     */
    private Map<String, String> evaluateAssessmentResult(int totalScore, int maxScore, String category) {
        String level;
        String suggestion;
        
        // 计算得分率
        double scoreRate = maxScore > 0 ? (double) totalScore / maxScore : 0;
        
        // 根据测评类别应用不同的评估标准
        if ("抑郁".equals(category)) {
            if (totalScore <= 10) {
                level = "正常";
                suggestion = "您的情绪状态良好，继续保持积极心态。建议：保持规律作息，多参加社交活动，培养兴趣爱好。";
            } else if (totalScore <= 16) {
                level = "轻度";
                suggestion = "您可能存在轻度抑郁倾向。建议：多参加户外活动，保持良好作息，适当放松心情，与亲友多交流。必要时可寻求心理咨询。";
            } else if (totalScore <= 24) {
                level = "中度";
                suggestion = "您可能存在中度抑郁症状。强烈建议：寻求专业心理咨询，进行心理疏导。同时注意调整生活方式，保证充足睡眠，适度运动。";
            } else {
                level = "重度";
                suggestion = "您可能存在重度抑郁症状。强烈建议：立即寻求专业心理医生帮助，进行系统治疗。必要时可考虑药物治疗配合心理治疗。请重视自己的心理健康！";
            }
        } else if ("焦虑".equals(category)) {
            if (totalScore <= 8) {
                level = "正常";
                suggestion = "您的焦虑水平正常，心态平和。建议：继续保持良好的生活习惯，适当进行放松训练。";
            } else if (totalScore <= 15) {
                level = "轻度";
                suggestion = "您可能存在轻度焦虑。建议：学习放松技巧（如深呼吸、冥想），规律运动，合理安排工作和休息。";
            } else if (totalScore <= 25) {
                level = "中度";
                suggestion = "您可能存在中度焦虑。建议：寻求心理咨询，学习焦虑管理技巧，必要时可进行认知行为治疗。";
            } else {
                level = "重度";
                suggestion = "您可能存在重度焦虑。强烈建议：尽快寻求专业心理治疗，可能需要配合药物治疗。请及时就医！";
            }
        } else if ("压力".equals(category)) {
            if (scoreRate <= 0.3) {
                level = "正常";
                suggestion = "您的压力水平正常，能够较好地应对生活和工作。建议：保持现有的压力管理方式。";
            } else if (scoreRate <= 0.5) {
                level = "轻度";
                suggestion = "您感受到一定压力。建议：合理安排时间，学会拒绝不必要的事务，培养兴趣爱好来释放压力。";
            } else if (scoreRate <= 0.7) {
                level = "中度";
                suggestion = "您的压力较大。建议：寻求社会支持，学习压力管理技巧，必要时可寻求专业帮助。";
            } else {
                level = "重度";
                suggestion = "您的压力过大。强烈建议：调整工作和生活节奏，寻求专业心理咨询，学习有效的压力应对策略。";
            }
        } else {
            // 其他类型测评的通用评估
            if (scoreRate <= 0.3) {
                level = "正常";
                suggestion = "测评结果在正常范围内，建议继续保持良好的心理状态。";
            } else if (scoreRate <= 0.5) {
                level = "轻度";
                suggestion = "测评显示您可能存在一些心理困扰。建议：适当调整生活方式，必要时可寻求心理咨询。";
            } else if (scoreRate <= 0.7) {
                level = "中度";
                suggestion = "测评显示您可能存在明显的心理困扰。建议：寻求专业心理咨询，进行系统的心理调适。";
            } else {
                level = "重度";
                suggestion = "测评显示您可能存在严重的心理困扰。强烈建议：尽快寻求专业心理治疗。";
            }
        }
        
        return Map.of("level", level, "suggestion", suggestion);
    }
    
    @Override
    public Map<String, Object> getAssessmentResult(Long assessmentId, Long userId) {
        // 从数据库获取最新的用户测评记录
        QueryWrapper<UserAssessment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("assessment_id", assessmentId)
                   .orderByDesc("create_time")
                   .last("LIMIT 1");
        
        UserAssessment userAssessment = userAssessmentMapper.selectOne(queryWrapper);
        
        if (userAssessment == null) {
            return null;
        }
        
        // 获取测评信息
        Assessment assessment = assessmentMapper.selectById(assessmentId);
        String assessmentTitle = assessment != null ? assessment.getTitle() : "心理测评";
        
        // 返回真实的测评结果，包含更多信息
        return Map.of(
            "score", userAssessment.getScore(),
            "level", userAssessment.getResultLevel(),
            "suggestion", userAssessment.getSuggestions(),
            "completedTime", userAssessment.getCreateTime().toString(),
            "assessmentTitle", assessmentTitle,
            "category", assessment != null ? assessment.getCategory() : ""
        );
    }
    
    @Override
    public List<Map<String, Object>> getUserAssessmentHistory(Long userId) {
        // 查询用户的所有测评记录
        QueryWrapper<UserAssessment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .eq("status", "已完成")
                   .orderByDesc("create_time");
        
        List<UserAssessment> userAssessments = userAssessmentMapper.selectList(queryWrapper);
        
        // 将结果转换为前端需要的格式
        List<Map<String, Object>> historyList = new ArrayList<>();
        
        for (UserAssessment ua : userAssessments) {
            // 获取测评问卷信息
            Assessment assessment = assessmentMapper.selectById(ua.getAssessmentId());
            
            Map<String, Object> historyItem = new java.util.HashMap<>();
            historyItem.put("id", ua.getId());
            historyItem.put("assessmentId", ua.getAssessmentId());
            historyItem.put("assessmentTitle", assessment != null ? assessment.getTitle() : "未知测评");
            historyItem.put("category", assessment != null ? assessment.getCategory() : "");
            historyItem.put("completionTime", ua.getCreateTime().toString());
            historyItem.put("totalScore", ua.getScore());
            historyItem.put("assessmentLevel", ua.getResultLevel());
            historyItem.put("suggestion", ua.getSuggestions());
            historyItem.put("status", ua.getStatus());
            
            historyList.add(historyItem);
        }
        
        return historyList;
    }
    
    @Override
    public void updateAssessmentQuestionsCount() {
        // 获取所有测评问卷
        List<Assessment> assessments = assessmentMapper.selectList(null);
        
        for (Assessment assessment : assessments) {
            // 统计每个测评的实际题目数量
            QueryWrapper<AssessmentQuestion> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("assessment_id", assessment.getId());
            Long questionCount = questionMapper.selectCount(queryWrapper);
            
            // 更新测评问卷的题目数量
            assessment.setQuestionsCount(questionCount.intValue());
            assessmentMapper.updateById(assessment);
            
            System.out.println("更新测评问卷 " + assessment.getTitle() + " 的题目数量为: " + questionCount);
        }
    }
}