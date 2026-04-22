package com.worker.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worker.demo1.entity.ConsultationMessage;
import com.worker.demo1.mapper.ConsultationMessageMapper;
import com.worker.demo1.service.ConsultationMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 专家咨询留言服务实现类
 */
@Service
public class ConsultationMessageServiceImpl extends ServiceImpl<ConsultationMessageMapper, ConsultationMessage> implements ConsultationMessageService {
    
    @Autowired
    private ConsultationMessageMapper messageMapper;
    
    @Override
    public boolean sendMessage(ConsultationMessage message) {
        try {
            message.setCreateTime(LocalDateTime.now());
            message.setIsRead(false);
            // 设置默认的senderId，如果是专家发送，设置为1（默认专家ID）
            if (message.getSenderId() == null) {
                message.setSenderId(1L); // 默认专家ID
            }
            // 设置消息类型
            if (message.getMessageType() == null) {
                message.setMessageType("text");
            }
            System.out.println("准备插入留言数据: " + message);
            int result = messageMapper.insert(message);
            System.out.println("插入结果: " + result);
            return result > 0;
        } catch (Exception e) {
            System.err.println("发送留言异常: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public List<ConsultationMessage> getMessagesByConsultationId(Long consultationId) {
        QueryWrapper<ConsultationMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consultation_id", consultationId)
                   .orderByAsc("create_time");
        return messageMapper.selectList(queryWrapper);
    }
    
    @Override
    public boolean markMessageAsRead(Long messageId) {
        ConsultationMessage message = messageMapper.selectById(messageId);
        if (message != null) {
            message.setIsRead(true);
            return messageMapper.updateById(message) > 0;
        }
        return false;
    }
    
    @Override
    public Long getUnreadMessageCount(Long consultationId, String userType) {
        QueryWrapper<ConsultationMessage> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("consultation_id", consultationId)
                   .eq("sender_type", userType.equals("user") ? "psychologist" : "user")
                   .eq("is_read", false);
        return messageMapper.selectCount(queryWrapper);
    }
}