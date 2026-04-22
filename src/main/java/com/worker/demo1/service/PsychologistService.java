package com.worker.demo1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worker.demo1.entity.Psychologist;
import java.math.BigDecimal;
import java.util.List;

/**
 * 心理专家服务接口
 */
public interface PsychologistService extends IService<Psychologist> {
    
    /**
     * 专家登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    Psychologist login(String username, String password);
    
    /**
     * 根据擅长领域查询专家
     * @param specialty 擅长领域
     * @return 专家列表
     */
    List<Psychologist> getPsychologistsBySpecialty(String specialty);
    
    /**
     * 获取可用的专家列表
     * @return 专家列表
     */
    List<Psychologist> getAvailablePsychologists();
    
    /**
     * 搜索专家（支持关键词、擅长领域、排序）
     * @param keyword 搜索关键词
     * @param specialty 擅长领域
     * @param sortBy 排序方式
     * @return 专家列表
     */
    List<Psychologist> searchPsychologists(String keyword, String specialty, String sortBy);
    
    /**
     * 更新专家评分
     * @param psychologistId 专家ID
     * @param rating 新评分
     */
    void updateRating(Long psychologistId, BigDecimal rating);
    
    /**
     * 心理专家注册
     * @param psychologist 专家信息
     * @return 注册结果
     */
    boolean register(Psychologist psychologist);
    
    /**
     * 重置密码
     * @param username 用户名
     * @param email 邮箱
     * @param newPassword 新密码
     * @return 重置结果
     */
    boolean resetPassword(String username, String email, String newPassword);
}