package com.worker.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * AI互动记录实体类
 * 对应数据库表：ai_interactions
 */
@TableName("ai_interactions")
public class AiInteraction {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("user_id")
    private Long userId;
    
    @TableField("interaction_type")
    private String interactionType;
    
    @TableField("user_message")
    private String userMessage;
    
    @TableField("ai_response")
    private String aiResponse;
    
    @TableField("emotion_analysis")
    private String emotionAnalysis; // JSON格式存储情绪分析结果
    
    @TableField("suggestion")
    private String suggestion;
    
    @TableField("session_id")
    private String sessionId;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getInteractionType() { return interactionType; }
    public void setInteractionType(String interactionType) { this.interactionType = interactionType; }
    
    public String getUserMessage() { return userMessage; }
    public void setUserMessage(String userMessage) { this.userMessage = userMessage; }
    
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    
    public String getEmotionAnalysis() { return emotionAnalysis; }
    public void setEmotionAnalysis(String emotionAnalysis) { this.emotionAnalysis = emotionAnalysis; }
    
    public String getSuggestion() { return suggestion; }
    public void setSuggestion(String suggestion) { this.suggestion = suggestion; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}