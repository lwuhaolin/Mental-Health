package com.worker.demo1.controller;

import com.worker.demo1.entity.AiInteraction;
import com.worker.demo1.service.AiInteractionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI互动控制器
 */
@RestController
@RequestMapping("/ai")
public class AiInteractionController {
    
    @Autowired
    private AiInteractionService aiService;
    
    /**
     * AI对话 - 疗愈室
     */
    @PostMapping("/therapy")
    public Map<String, Object> therapyChat(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = data.get("userId") != null ? Long.valueOf(data.get("userId").toString()) : null;
            String message = data.get("message").toString();
            
            String aiResponse = aiService.chatWithAI(userId, message, "AI疗愈");
            
            // 检查AI响应是否包含错误信息
            if (aiResponse.contains("AI服务暂时不可用") || aiResponse.contains("AI服务调用失败")) {
                result.put("success", false);
                result.put("message", aiResponse);
            } else {
                result.put("success", true);
                result.put("data", Map.of(
                    "response", aiResponse,
                    "type", "therapy"
                ));
                result.put("message", "AI回复成功");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "AI服务异常：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * AI对话 - 心理评估
     */
    @PostMapping("/assessment")
    public Map<String, Object> assessmentChat(@RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = data.get("userId") != null ? Long.valueOf(data.get("userId").toString()) : null;
            String message = data.get("message").toString();
            
            String aiResponse = aiService.chatWithAI(userId, message, "AI评估");
            
            // 检查AI响应是否包含错误信息
            if (aiResponse.contains("AI服务暂时不可用") || aiResponse.contains("AI服务调用失败")) {
                result.put("success", false);
                result.put("message", aiResponse);
            } else {
                result.put("success", true);
                result.put("data", Map.of(
                    "response", aiResponse,
                    "type", "assessment"
                ));
                result.put("message", "AI评估回复成功");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "AI评估服务异常：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 获取对话历史
     */
    @GetMapping("/history")
    public Map<String, Object> getChatHistory(@RequestParam Long userId, @RequestParam(required = false) String chatType) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 这里简化处理，获取用户最近的对话记录
            List<AiInteraction> history = aiService.getChatHistory(userId, "recent");
            result.put("success", true);
            result.put("data", history);
            result.put("message", "获取聊天历史成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取历史记录失败");
        }
        return result;
    }
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Map<String, Object> healthCheck() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("data", Map.of(
            "status", "healthy",
            "service", "AI心理助手",
            "model", "通义千问-qwen-plus"
        ));
        result.put("message", "AI服务运行正常");
        return result;
    }
    
    /**
     * 删除单条对话历史记录
     */
    @DeleteMapping("/history/{interactionId}")
    public Map<String, Object> deleteHistory(@PathVariable Long interactionId, @RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = aiService.deleteHistory(userId, interactionId);
            if (success) {
                result.put("success", true);
                result.put("message", "删除成功");
            } else {
                result.put("success", false);
                result.put("message", "删除失败，记录不存在或无权限");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除失败：" + e.getMessage());
        }
        return result;
    }
    
    /**
     * 清空用户的所有对话历史
     */
    @DeleteMapping("/history/clear")
    public Map<String, Object> clearHistory(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            boolean success = aiService.clearHistory(userId);
            if (success) {
                result.put("success", true);
                result.put("message", "历史记录已清空");
            } else {
                result.put("success", false);
                result.put("message", "清空失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "清空失败：" + e.getMessage());
        }
        return result;
    }

}