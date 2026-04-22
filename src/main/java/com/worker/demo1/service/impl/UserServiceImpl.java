package com.worker.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worker.demo1.entity.User;
import com.worker.demo1.mapper.UserMapper;
import com.worker.demo1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * 用户服务实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    
    @Autowired
    private UserMapper userMapper;
    
    @Override
    public User login(String username, String password) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username)
                   .eq("password", password);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            String s = user.getStatus();
            boolean isNormal = s != null && ("正常".equals(s) || "1".equals(s) || s.contains("正"));
            if (!isNormal) {
                return null;
            }
            updateLastLoginTime(user.getId());
        }
        return user;
    }
    
    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User existingUser = userMapper.selectOne(queryWrapper);
        if (existingUser != null) {
            return false; // 用户名已存在
        }
        
        // 设置默认值
        user.setStatus("正常");
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        
        return userMapper.insert(user) > 0;
    }
    
    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        return userMapper.selectOne(queryWrapper);
    }
    
    @Override
    public void updateLastLoginTime(Long userId) {
        User user = new User();
        user.setId(userId);
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);
    }
    
    @Override
    public boolean resetPassword(String username, String email, String newPassword) {
        // 验证用户名和邮箱是否匹配
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username)
                   .eq("email", email)
                   .eq("status", "正常");
        User user = userMapper.selectOne(queryWrapper);
        
        if (user == null) {
            return false; // 用户名和邮箱不匹配
        }
        
        // 更新密码
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setPassword(newPassword);
        updateUser.setUpdateTime(LocalDateTime.now());
        
        return userMapper.updateById(updateUser) > 0;
    }
    
    @Override
    public long getActiveUserCount() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.ge("last_login_time", LocalDateTime.now().minusDays(30))
                   .eq("status", "正常");
        return userMapper.selectCount(queryWrapper);
    }
}