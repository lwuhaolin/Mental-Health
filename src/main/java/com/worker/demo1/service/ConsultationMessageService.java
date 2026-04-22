package com.worker.demo1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worker.demo1.entity.ConsultationMessage;
import java.util.List;

/**
 * 专家咨询留言服务接口
 */
public interface ConsultationMessageService extends IService<ConsultationMessage> {
    
    /**
     * 发送留言
     */
    boolean sendMessage(ConsultationMessage message);
    
    /**
     * 获取咨询的留言列表
     */
    List<ConsultationMessage> getMessagesByConsultationId(Long consultationId);
    
    /**
     * 标记留言为已读
     */
    boolean markMessageAsRead(Long messageId);
    
    /**
     * 获取未读留言数量
     */
    Long getUnreadMessageCount(Long consultationId, String userType);
}