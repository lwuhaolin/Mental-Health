package com.worker.demo1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worker.demo1.entity.ContactMessage;
import com.worker.demo1.mapper.ContactMessageMapper;
import com.worker.demo1.service.ContactMessageService;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 联系我们留言服务实现类
 */
@Service
public class ContactMessageServiceImpl extends ServiceImpl<ContactMessageMapper, ContactMessage> implements ContactMessageService {
    
    @Override
    public ContactMessage submitMessage(ContactMessage contactMessage) {
        contactMessage.setStatus("pending");
        this.save(contactMessage);
        return contactMessage;
    }
    
    @Override
    public List<ContactMessage> getAllMessages() {
        return this.list();
    }
    
    @Override
    public List<ContactMessage> getMessagesByStatus(String status) {
        return this.lambdaQuery()
                .eq(ContactMessage::getStatus, status)
                .list();
    }
    
    @Override
    public boolean replyMessage(Long messageId, String replyContent) {
        ContactMessage message = this.getById(messageId);
        if (message != null) {
            message.setStatus("replied");
            message.setReplyContent(replyContent);
            return this.updateById(message);
        }
        return false;
    }
}