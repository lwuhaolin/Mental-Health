package com.worker.demo1.controller;

import com.worker.demo1.entity.ContactMessage;
import com.worker.demo1.entity.Psychologist;
import com.worker.demo1.service.ContactMessageService;
import com.worker.demo1.service.PsychologistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系我们页面控制器
 */
@RestController
@RequestMapping("/contact")
public class ContactController {
    
    @Autowired
    private ContactMessageService contactMessageService;
    
    @Autowired
    private PsychologistService psychologistService;
    
    /**
     * 提交留言
     */
    @PostMapping("/submit")
    public Map<String, Object> submitMessage(@RequestBody ContactMessage contactMessage) {
        Map<String, Object> result = new HashMap<>();
        try {
            ContactMessage savedMessage = contactMessageService.submitMessage(contactMessage);
            result.put("success", true);
            result.put("message", "留言提交成功！我们会在24小时内回复您。");
            result.put("data", savedMessage);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "留言提交失败，请稍后重试");
        }
        return result;
    }
    
    /**
     * 获取团队专家列表
     */
    @GetMapping("/team")
    public Map<String, Object> getTeamPsychologists() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Psychologist> psychologists = psychologistService.getAvailablePsychologists();
            result.put("success", true);
            result.put("psychologists", psychologists);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取专家列表失败");
        }
        return result;
    }
    
    /**
     * 获取所有留言（管理员用）
     */
    @GetMapping("/messages")
    public Map<String, Object> getAllMessages() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<ContactMessage> messages = contactMessageService.getAllMessages();
            result.put("success", true);
            result.put("messages", messages);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取留言列表失败");
        }
        return result;
    }
    
    /**
     * 回复留言（管理员用）
     */
    @PostMapping("/reply")
    public Map<String, Object> replyMessage(@RequestBody Map<String, Object> replyData) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long messageId = Long.valueOf(replyData.get("messageId").toString());
            String replyContent = replyData.get("replyContent").toString();
            
            boolean success = contactMessageService.replyMessage(messageId, replyContent);
            if (success) {
                result.put("success", true);
                result.put("message", "回复成功");
            } else {
                result.put("success", false);
                result.put("message", "回复失败，留言不存在");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "回复失败");
        }
        return result;
    }
}