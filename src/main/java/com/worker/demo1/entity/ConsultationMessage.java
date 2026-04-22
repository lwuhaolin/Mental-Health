package com.worker.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 专家咨询留言实体类
 * 对应数据库表：consultation_messages
 */
@TableName("consultation_messages")
public class ConsultationMessage {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("consultation_id")
    private Long consultationId;
    
    @TableField("sender_id")
    private Long senderId;
    
    @TableField("sender_type")
    private String senderType; // 'user' 或 'psychologist'
    
    @TableField("message_content")
    private String messageContent;
    
    @TableField("message_type")
    private String messageType; // 'text', 'image', 'file'
    
    @TableField("is_read")
    private Boolean isRead;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getConsultationId() { return consultationId; }
    public void setConsultationId(Long consultationId) { this.consultationId = consultationId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public String getSenderType() { return senderType; }
    public void setSenderType(String senderType) { this.senderType = senderType; }
    
    public String getMessageContent() { return messageContent; }
    public void setMessageContent(String messageContent) { this.messageContent = messageContent; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}