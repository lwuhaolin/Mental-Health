    package com.worker.demo1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worker.demo1.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户信息
     * @return 注册结果
     */
    boolean register(User user);
    
    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return 用户信息
     */
    User getUserByUsername(String username);
    
    /**
     * 更新用户最后登录时间
     * @param userId 用户ID
     */
    void updateLastLoginTime(Long userId);
    
    /**
     * 重置密码
     * @param username 用户名
     * @param email 邮箱
     * @param newPassword 新密码
     * @return 重置结果
     */
    boolean resetPassword(String username, String email, String newPassword);
    
    /**
     * 获取活跃用户数（最近30天登录的用户）
     * @return 活跃用户数量
     */
    long getActiveUserCount();
}