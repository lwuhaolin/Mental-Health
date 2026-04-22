package com.worker.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worker.demo1.entity.AiInteraction;
import com.worker.demo1.mapper.AiInteractionMapper;
import com.worker.demo1.service.AiInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * AI互动服务实现类 - 使用HTTP客户端直接调用通义千问API
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiInteractionServiceImpl extends ServiceImpl<AiInteractionMapper, AiInteraction> implements AiInteractionService {
    
    @Autowired
    private AiInteractionMapper aiInteractionMapper;
    
    @Value("${spring.ai.openai.api-key}")
    private String apiKey;
    
    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.options.model:qwen3.5-plus}")
    private String model;

    @Value("${spring.ai.openai.chat.options.temperature:0.7}")
    private Double temperature;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public String chatWithAI(Long userId, String message, String interactionType) {
        // 生成或获取会话ID
        String sessionId = generateSessionId(userId);
        
        // 根据互动类型构建系统提示词
        String systemPrompt = getSystemPromptByType(interactionType, message);
        
        // 调用通义千问API
        String aiResponse = callTongyiQianwenAPI(systemPrompt, message);
        
        // 保存互动记录到AI互动表
        AiInteraction interaction = new AiInteraction();
        interaction.setUserId(userId);
        interaction.setInteractionType(interactionType);
        interaction.setUserMessage(message);
        interaction.setAiResponse(aiResponse);
        interaction.setSessionId(sessionId);
        interaction.setCreateTime(LocalDateTime.now());
        
        aiInteractionMapper.insert(interaction);
        
        return aiResponse;
    }
    
    /**
     * 调用通义千问API
     */
    private String callTongyiQianwenAPI(String systemPrompt, String userMessage) {
        try {
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", model);
            requestBody.put("temperature", temperature);
            
            List<Map<String, String>> messages = new ArrayList<>();
            messages.add(Map.of("role", "system", "content", systemPrompt));
            messages.add(Map.of("role", "user", "content", userMessage));
            requestBody.put("messages", messages);

            log.debug("Calling DashScope compatible API. baseUrl={}, model={}, temperature={}", baseUrl, model, temperature);
            
            // 发送请求
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    baseUrl + "/chat/completions",
                    HttpMethod.POST,
                    request,
                    new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            // 解析响应
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) body.get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    return message.get("content").toString();
                }
            }
            
            return "抱歉，AI服务暂时不可用，请稍后再试。";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "AI服务调用失败：" + e.getMessage();
        }
    }
    
    @Override
    public List<AiInteraction> getChatHistory(Long userId, String sessionId) {
        QueryWrapper<AiInteraction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        
        if ("recent".equals(sessionId)) {
            // 获取最近10条对话记录
            queryWrapper.orderByDesc("create_time")
                       .last("LIMIT 10");
        } else {
            queryWrapper.eq("session_id", sessionId)
                       .orderByAsc("create_time");
        }
        
        return aiInteractionMapper.selectList(queryWrapper);
    }
    
    /**
     * 保存AI互动记录
     */
    public void saveAiInteraction(AiInteraction interaction) {
        aiInteractionMapper.insert(interaction);
    }
    
    @Override
    public String psychologicalAssessment(Long userId, String assessmentData) {
        String prompt = "请对以下心理评估数据进行分析：" + assessmentData + 
                       "请给出专业的心理评估结果和建议。";
        
        return chatWithAI(userId, prompt, "AI评估");
    }
    
    @Override
    public String healingConversation(Long userId, String userMessage) {
        String prompt = "作为心理疗愈助手，请对以下用户倾诉进行温暖、专业的回应：" + userMessage + 
                       "请用温暖、理解的语言进行疗愈对话。";
        
        return chatWithAI(userId, prompt, "AI疗愈");
    }
    
    private String generateSessionId(Long userId) {
        return "session_" + userId + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * 根据互动类型获取系统提示词
     */
    private String getSystemPromptByType(String interactionType, String userMessage) {
        switch (interactionType) {
            case "AI疗愈":
                return """
                    你是一位专业的心理治疗师，专门提供心理支持和情绪疏导。
                    请用温暖、专业、富有同理心的方式回应用户。
                    重点提供情绪支持、压力管理建议和心理疏导。
                    保持积极、建设性的态度，避免给出医疗诊断。
                    每次回复控制在200字以内，确保回复专业且易于理解。
                    """;
                    
            case "AI评估":
                return """
                    你是一位专业的心理评估专家，负责进行心理状态评估和分析。
                    请用专业、客观、科学的方式回应用户。
                    重点进行心理状态分析、风险评估和改善建议。
                    基于用户描述提供初步评估，并给出具体的改善建议。
                    避免给出明确的医疗诊断，强调专业咨询的重要性。
                    每次回复控制在300字以内，确保评估专业且实用。
                    """;
                    
            default:
                return """
                    你是一位专业的心理助手，能够提供心理支持和专业建议。
                    请用温暖、专业的方式回应用户的问题和需求。
                    保持积极、建设性的沟通态度。
                    """;
        }
    }
    
    @Override
    public boolean deleteHistory(Long userId, Long interactionId) {
        try {
            QueryWrapper<AiInteraction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", interactionId)
                       .eq("user_id", userId); // 确保只能删除自己的记录
            
            int result = aiInteractionMapper.delete(queryWrapper);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean clearHistory(Long userId) {
        try {
            QueryWrapper<AiInteraction> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("user_id", userId);
            
            int result = aiInteractionMapper.delete(queryWrapper);
            return result >= 0; // 即使没有记录也返回true
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}