package com.worker.demo1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worker.demo1.entity.AiInteraction;
import java.util.List;

/**
 * AI互动服务接口
 */
public interface AiInteractionService extends IService<AiInteraction> {
    
    /**
     * 与AI对话
     * @param userId 用户ID
     * @param message 用户消息
     * @param interactionType 互动类型
     * @return AI回复
     */
    String chatWithAI(Long userId, String message, String interactionType);
    
    /**
     * 获取用户AI对话历史
     * @param userId 用户ID
     * @param sessionId 会话ID
     * @return 对话历史列表
     */
    List<AiInteraction> getChatHistory(Long userId, String sessionId);
    
    /**
     * AI心理评估
     * @param userId 用户ID
     * @param assessmentData 评估数据
     * @return 评估结果
     */
    String psychologicalAssessment(Long userId, String assessmentData);
    
    /**
     * AI疗愈对话
     * @param userId 用户ID
     * @param userMessage 用户消息
     * @return 疗愈回复
     */
    String healingConversation(Long userId, String userMessage);
    
    /**
     * 保存AI互动记录
     * @param interaction AI互动记录
     */
    void saveAiInteraction(AiInteraction interaction);
    
    /**
     * 删除单条AI对话历史记录
     * @param userId 用户ID
     * @param interactionId 互动记录ID
     * @return 是否删除成功
     */
    boolean deleteHistory(Long userId, Long interactionId);
    
    /**
     * 清空用户的所有AI对话历史
     * @param userId 用户ID
     * @return 是否清空成功
     */
    boolean clearHistory(Long userId);
}