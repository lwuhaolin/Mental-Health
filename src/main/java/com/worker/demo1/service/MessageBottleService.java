package com.worker.demo1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worker.demo1.entity.MessageBottle;
import com.worker.demo1.entity.BottleReply;
import java.util.List;
import java.util.Map;

/**
 * 漂流瓶服务接口
 */
public interface MessageBottleService extends IService<MessageBottle> {
    
    /**
     * 发布漂流瓶
     * @param messageBottle 漂流瓶信息
     * @return 发布结果
     */
    boolean publishBottle(MessageBottle messageBottle);
    
    /**
     * 回复漂流瓶
     * @param bottleReply 回复信息
     * @return 回复结果
     */
    boolean replyBottle(BottleReply bottleReply);
    
    /**
     * 获取漂流瓶列表（随机获取）
     * @param count 数量
     * @return 漂流瓶列表
     */
    List<MessageBottle> getRandomBottles(Integer count);
    
    /**
     * 获取漂流瓶列表（带用户名）
     * @param count 数量
     * @return 漂流瓶列表（包含用户名）
     */
    List<Map<String, Object>> getRandomBottlesWithUsername(Integer count);
    
    /**
     * 获取漂流瓶回复
     * @param bottleId 漂流瓶ID
     * @return 回复列表
     */
    List<BottleReply> getBottleReplies(Long bottleId);
    
    /**
     * 获取漂流瓶回复（带用户名）
     * @param bottleId 漂流瓶ID
     * @return 回复列表（包含用户名）
     */
    List<Map<String, Object>> getBottleRepliesWithUsername(Long bottleId);
    
    /**
     * 点赞漂流瓶
     * @param bottleId 漂流瓶ID
     * @return 点赞结果
     */
    boolean likeBottle(Long bottleId);
    
    /**
     * 点赞回复
     * @param replyId 回复ID
     * @return 点赞结果
     */
    boolean likeReply(Long replyId);
    
    /**
     * 删除回复
     * @param replyId 回复ID
     * @return 删除结果
     */
    boolean deleteReply(Long replyId);
}