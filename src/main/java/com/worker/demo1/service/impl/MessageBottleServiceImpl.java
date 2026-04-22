package com.worker.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worker.demo1.entity.MessageBottle;
import com.worker.demo1.entity.BottleReply;
import com.worker.demo1.entity.User;
import com.worker.demo1.mapper.MessageBottleMapper;
import com.worker.demo1.mapper.BottleReplyMapper;
import com.worker.demo1.mapper.UserMapper;
import com.worker.demo1.service.MessageBottleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 漂流瓶服务实现类
 */
@Service
public class MessageBottleServiceImpl extends ServiceImpl<MessageBottleMapper, MessageBottle> implements MessageBottleService {
    
    @Autowired
    private MessageBottleMapper bottleMapper;
    
    @Autowired
    private BottleReplyMapper replyMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public boolean publishBottle(MessageBottle messageBottle) {
        // 设置默认值
        if (messageBottle.getStatus() == null || messageBottle.getStatus().isEmpty()) {
            messageBottle.setStatus("normal");
        }
        if (messageBottle.getReplyCount() == null) {
            messageBottle.setReplyCount(0);
        }
        if (messageBottle.getLikeCount() == null) {
            messageBottle.setLikeCount(0);
        }
        if (messageBottle.getIsAnonymous() == null) {
            messageBottle.setIsAnonymous(false);
        }
        if (messageBottle.getMood() == null || messageBottle.getMood().isEmpty()) {
            messageBottle.setMood("calm");
        }
        if (messageBottle.getBottleType() == null || messageBottle.getBottleType().isEmpty()) {
            messageBottle.setBottleType("mood");
        }
        
        try {
            return bottleMapper.insert(messageBottle) > 0;
        } catch (Exception e) {
            // 打印详细错误信息方便调试
            e.printStackTrace();
            throw new RuntimeException("发布漂流瓶失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean replyBottle(BottleReply bottleReply) {
        // 设置默认值
        if (bottleReply.getStatus() == null || bottleReply.getStatus().isEmpty()) {
            bottleReply.setStatus("normal");
        }
        if (bottleReply.getLikeCount() == null) {
            bottleReply.setLikeCount(0);
        }
        if (bottleReply.getIsAnonymous() == null) {
            bottleReply.setIsAnonymous(false);
        }
        
        try {
            boolean result = replyMapper.insert(bottleReply) > 0;
            if (result) {
                // 更新漂流瓶的回复数量
                MessageBottle bottle = bottleMapper.selectById(bottleReply.getBottleId());
                if (bottle != null) {
                    Integer currentCount = bottle.getReplyCount();
                    bottle.setReplyCount(currentCount != null ? currentCount + 1 : 1);
                    bottleMapper.updateById(bottle);
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("回复漂流瓶失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<MessageBottle> getRandomBottles(Integer count) {
        QueryWrapper<MessageBottle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "normal")
                   .orderByAsc("RAND()")
                   .last("LIMIT " + count);
        return bottleMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<BottleReply> getBottleReplies(Long bottleId) {
        QueryWrapper<BottleReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("bottle_id", bottleId)
                   .eq("status", "normal")
                   .orderByAsc("create_time");
        return replyMapper.selectList(queryWrapper);
    }
    
    @Override
    public boolean likeBottle(Long bottleId) {
        try {
            MessageBottle bottle = bottleMapper.selectById(bottleId);
            if (bottle != null) {
                Integer currentCount = bottle.getLikeCount();
                bottle.setLikeCount(currentCount != null ? currentCount + 1 : 1);
                return bottleMapper.updateById(bottle) > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("点赞漂流瓶失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean likeReply(Long replyId) {
        try {
            BottleReply reply = replyMapper.selectById(replyId);
            if (reply != null) {
                Integer currentCount = reply.getLikeCount();
                reply.setLikeCount(currentCount != null ? currentCount + 1 : 1);
                return replyMapper.updateById(reply) > 0;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("点赞回复失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getRandomBottlesWithUsername(Integer count) {
        try {
            // 获取漂流瓶列表
            List<MessageBottle> bottles = getRandomBottles(count);
            
            // 转换为包含用户名的Map列表
            return bottles.stream().map(bottle -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", bottle.getId());
                map.put("userId", bottle.getUserId());
                map.put("content", bottle.getContent());
                map.put("bottleType", bottle.getBottleType());
                map.put("mood", bottle.getMood());
                map.put("isAnonymous", bottle.getIsAnonymous());
                map.put("replyCount", bottle.getReplyCount());
                map.put("likeCount", bottle.getLikeCount());
                map.put("status", bottle.getStatus());
                map.put("createTime", bottle.getCreateTime());
                
                // 根据是否匿名返回用户名
                if (bottle.getIsAnonymous() != null && bottle.getIsAnonymous()) {
                    map.put("username", "匿名用户");
                } else {
                    User user = userMapper.selectById(bottle.getUserId());
                    map.put("username", user != null ? user.getUsername() : "未知用户");
                }
                
                return map;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取漂流瓶列表失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Map<String, Object>> getBottleRepliesWithUsername(Long bottleId) {
        try {
            // 获取回复列表
            List<BottleReply> replies = getBottleReplies(bottleId);
            
            // 转换为包含用户名的Map列表
            return replies.stream().map(reply -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", reply.getId());
                map.put("bottleId", reply.getBottleId());
                map.put("userId", reply.getReplyUserId());
                map.put("content", reply.getContent());
                map.put("isAnonymous", reply.getIsAnonymous());
                map.put("likeCount", reply.getLikeCount());
                map.put("status", reply.getStatus());
                map.put("createTime", reply.getCreateTime());
                
                // 根据是否匿名返回用户名
                if (reply.getIsAnonymous() != null && reply.getIsAnonymous()) {
                    map.put("username", "匿名用户");
                } else {
                    User user = userMapper.selectById(reply.getReplyUserId());
                    map.put("username", user != null ? user.getUsername() : "未知用户");
                }
                
                return map;
            }).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("获取回复列表失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean deleteReply(Long replyId) {
        try {
            // 获取回复信息
            BottleReply reply = replyMapper.selectById(replyId);
            if (reply != null) {
                // 删除回复
                boolean result = replyMapper.deleteById(replyId) > 0;
                if (result) {
                    // 更新漂流瓶的回复数量
                    MessageBottle bottle = bottleMapper.selectById(reply.getBottleId());
                    if (bottle != null) {
                        Integer currentCount = bottle.getReplyCount();
                        bottle.setReplyCount(currentCount != null && currentCount > 0 ? currentCount - 1 : 0);
                        bottleMapper.updateById(bottle);
                    }
                }
                return result;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("删除回复失败: " + e.getMessage(), e);
        }
    }
}