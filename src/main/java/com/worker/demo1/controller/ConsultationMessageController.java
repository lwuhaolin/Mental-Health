package com.worker.demo1.controller;

import com.worker.demo1.entity.ConsultationMessage;
import com.worker.demo1.service.ConsultationMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专家咨询留言控制器
 */
@RestController
@RequestMapping("/consultation/message")
public class ConsultationMessageController {
    
    @Autowired
    private ConsultationMessageService messageService;
    
    /**
     * 发送留言
     */
    @PostMapping("/send")
    public Map<String, Object> sendMessage(@RequestBody ConsultationMessage message) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.sendMessage(message);
            if (success) {
                result.put("code", 200);
                result.put("message", "留言发送成功");
                result.put("success", true);
            } else {
                result.put("code", 500);
                result.put("message", "留言发送失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "留言发送失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }
    
    /**
     * 获取咨询的留言列表
     */
    @GetMapping("/list/{consultationId}")
    public Map<String, Object> getMessages(@PathVariable Long consultationId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ConsultationMessage> messages = messageService.getMessagesByConsultationId(consultationId);
            result.put("code", 200);
            result.put("message", "获取留言列表成功");
            result.put("data", messages);
            result.put("success", true);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取留言列表失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }
    
    /**
     * 标记留言为已读
     */
    @PostMapping("/read/{messageId}")
    public Map<String, Object> markAsRead(@PathVariable Long messageId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = messageService.markMessageAsRead(messageId);
            if (success) {
                result.put("code", 200);
                result.put("message", "标记已读成功");
                result.put("success", true);
            } else {
                result.put("code", 500);
                result.put("message", "标记已读失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "标记已读失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }
    
    /**
     * 获取未读留言数量
     */
    @GetMapping("/unread/count")
    public Map<String, Object> getUnreadCount(@RequestParam Long consultationId, 
                                             @RequestParam String userType) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long count = messageService.getUnreadMessageCount(consultationId, userType);
            result.put("code", 200);
            result.put("message", "获取未读数量成功");
            result.put("data", count);
            result.put("success", true);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("message", "获取未读数量失败: " + e.getMessage());
            result.put("success", false);
        }
        return result;
    }
}