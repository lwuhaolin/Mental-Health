package com.worker.demo1.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worker.demo1.entity.Admin;
import com.worker.demo1.mapper.AdminMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AdminService extends ServiceImpl<AdminMapper, Admin> {
    
    @Autowired
    private AdminMapper adminMapper;
    
    // 管理员相关业务逻辑
    public Admin getAdminByUsername(String username) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username)
                   .eq("status", "正常");
        return adminMapper.selectOne(queryWrapper);
    }
    
    // 验证管理员登录
    public boolean validateAdmin(String username, String password) {
        Admin admin = getAdminByUsername(username);
        return admin != null && admin.getPassword().equals(password);
    }
    
    // 管理员登录方法
    public Admin login(String username, String password) {
        Admin admin = getAdminByUsername(username);
        if (admin != null && admin.getPassword().equals(password)) {
            // 更新最后登录时间
            updateLastLoginTime(admin.getId());
            return admin;
        }
        return null;
    }
    
    // 更新最后登录时间
    public void updateLastLoginTime(Long adminId) {
        Admin admin = new Admin();
        admin.setId(adminId);
        admin.setLastLoginTime(LocalDateTime.now());
        adminMapper.updateById(admin);
    }
}