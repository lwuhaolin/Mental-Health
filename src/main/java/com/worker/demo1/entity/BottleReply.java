package com.worker.demo1.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

/**
 * 漂流瓶回复实体类
 * 对应数据库表：bottle_replies
 */
@TableName("bottle_replies")
public class BottleReply {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    @TableField("bottle_id")
    private Long bottleId;
    
    @TableField("reply_user_id")
    private Long replyUserId;
    
    @TableField("content")
    private String content;
    
    @TableField("is_anonymous")
    private Boolean isAnonymous;
    
    @TableField("like_count")
    private Integer likeCount;
    
    @TableField("status")
    private String status;
    
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    // Getter and Setter methods
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getBottleId() { return bottleId; }
    public void setBottleId(Long bottleId) { this.bottleId = bottleId; }
    
    public Long getReplyUserId() { return replyUserId; }
    public void setReplyUserId(Long replyUserId) { this.replyUserId = replyUserId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
}