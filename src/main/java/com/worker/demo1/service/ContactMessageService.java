package com.worker.demo1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worker.demo1.entity.ContactMessage;
import java.util.List;

/**
 * 联系我们留言服务接口
 */
public interface ContactMessageService extends IService<ContactMessage> {
    
    /**
     * 提交留言
     */
    ContactMessage submitMessage(ContactMessage contactMessage);
    
    /**
     * 获取所有留言
     */
    List<ContactMessage> getAllMessages();
    
    /**
     * 根据状态获取留言
     */
    List<ContactMessage> getMessagesByStatus(String status);
    
    /**
     * 回复留言
     */
    boolean replyMessage(Long messageId, String replyContent);
}