package com.worker.demo1.controller;

import com.worker.demo1.entity.MessageBottle;
import com.worker.demo1.entity.BottleReply;
import com.worker.demo1.service.MessageBottleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 漂流瓶控制器
 */
@RestController
@RequestMapping("/bottle")
public class MessageBottleController {
    
    @Autowired
    private MessageBottleService bottleService;
    
    /**
     * 发布漂流瓶
     */
    @PostMapping("/publish")
    public Map<String, Object> publishBottle(@RequestBody MessageBottle messageBottle) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bottleService.publishBottle(messageBottle);
        if (success) {
            result.put("success", true);
            result.put("message", "发布成功");
        } else {
            result.put("success", false);
            result.put("message", "发布失败");
        }
        return result;
    }
    
    /**
     * 回复漂流瓶
     */
    @PostMapping("/reply")
    public Map<String, Object> replyBottle(@RequestBody BottleReply bottleReply) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bottleService.replyBottle(bottleReply);
        if (success) {
            result.put("success", true);
            result.put("message", "回复成功");
        } else {
            result.put("success", false);
            result.put("message", "回复失败");
        }
        return result;
    }
    
    /**
     * 获取随机漂流瓶（带用户名）
     */
    @GetMapping("/random")
    public Map<String, Object> getRandomBottles(@RequestParam(defaultValue = "10") Integer count) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> bottles = bottleService.getRandomBottlesWithUsername(count);
        result.put("success", true);
        result.put("bottles", bottles);
        return result;
    }
    
    /**
     * 获取漂流瓶回复（带用户名）
     */
    @GetMapping("/replies/{bottleId}")
    public Map<String, Object> getBottleReplies(@PathVariable Long bottleId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> replies = bottleService.getBottleRepliesWithUsername(bottleId);
        result.put("success", true);
        result.put("replies", replies);
        return result;
    }
    
    /**
     * 点赞漂流瓶
     */
    @PostMapping("/like/{bottleId}")
    public Map<String, Object> likeBottle(@PathVariable Long bottleId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bottleService.likeBottle(bottleId);
        if (success) {
            result.put("success", true);
            result.put("message", "点赞成功");
        } else {
            result.put("success", false);
            result.put("message", "点赞失败");
        }
        return result;
    }
    
    /**
     * 点赞回复
     */
    @PostMapping("/reply/like/{replyId}")
    public Map<String, Object> likeReply(@PathVariable Long replyId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bottleService.likeReply(replyId);
        if (success) {
            result.put("success", true);
            result.put("message", "点赞成功");
        } else {
            result.put("success", false);
            result.put("message", "点赞失败");
        }
        return result;
    }
    
    /**
     * 删除漂流瓶
     */
    @DeleteMapping("/delete/{bottleId}")
    public Map<String, Object> deleteBottle(@PathVariable Long bottleId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bottleService.removeById(bottleId);
        if (success) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败");
        }
        return result;
    }
    
    /**
     * 删除回复
     */
    @DeleteMapping("/reply/delete/{replyId}")
    public Map<String, Object> deleteReply(@PathVariable Long replyId) {
        Map<String, Object> result = new HashMap<>();
        boolean success = bottleService.deleteReply(replyId);
        if (success) {
            result.put("success", true);
            result.put("message", "删除成功");
        } else {
            result.put("success", false);
            result.put("message", "删除失败");
        }
        return result;
    }
}