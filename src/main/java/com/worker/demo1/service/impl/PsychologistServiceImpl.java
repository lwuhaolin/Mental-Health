package com.worker.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worker.demo1.entity.Psychologist;
import com.worker.demo1.mapper.PsychologistMapper;
import com.worker.demo1.service.PsychologistService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 心理专家服务实现类
 */
@Service
public class PsychologistServiceImpl 
    extends ServiceImpl<PsychologistMapper, Psychologist> 
    implements PsychologistService {
    
    @Override
    public Psychologist login(String username, String password) {
        LambdaQueryWrapper<Psychologist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Psychologist::getUsername, username)
               .eq(Psychologist::getPassword, password);
        Psychologist p = getOne(wrapper);
        if (p == null) return null;
        String s = p.getStatus();
        boolean isNormal = s != null && ("正常".equals(s) || "1".equals(s) || s.contains("正"));
        return isNormal ? p : null;
    }
    
    @Override
    public List<Psychologist> getPsychologistsBySpecialty(String specialty) {
        LambdaQueryWrapper<Psychologist> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Psychologist::getSpecialty, specialty)
               .eq(Psychologist::getStatus, "正常");
        return list(wrapper);
    }
    
    @Override
    public List<Psychologist> getAvailablePsychologists() {
        LambdaQueryWrapper<Psychologist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Psychologist::getStatus, "正常")
               .orderByDesc(Psychologist::getRating);
        return list(wrapper);
    }
    
    @Override
    public List<Psychologist> searchPsychologists(String keyword, String specialty, String sortBy) {
        LambdaQueryWrapper<Psychologist> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Psychologist::getStatus, "正常");
        
        // 关键词搜索（姓名或擅长领域）
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.and(w -> w.like(Psychologist::getRealName, keyword)
                             .or()
                             .like(Psychologist::getSpecialty, keyword));
        }
        
        // 擅长领域筛选
        if (specialty != null && !specialty.trim().isEmpty()) {
            wrapper.like(Psychologist::getSpecialty, specialty);
        }
        
        // 排序方式
        if ("rating_desc".equals(sortBy)) {
            wrapper.orderByDesc(Psychologist::getRating);
        } else if ("experience_desc".equals(sortBy)) {
            wrapper.orderByDesc(Psychologist::getExperienceYears);
        } else if ("price_asc".equals(sortBy)) {
            wrapper.orderByAsc(Psychologist::getHourlyRate);
        } else {
            wrapper.orderByDesc(Psychologist::getRating); // 默认按评分降序
        }
        
        return list(wrapper);
    }
    
    @Override
    public void updateRating(Long psychologistId, BigDecimal rating) {
        Psychologist psychologist = getById(psychologistId);
        if (psychologist != null) {
            psychologist.setRating(rating);
            updateById(psychologist);
        }
    }
    
    @Override
    public boolean register(Psychologist psychologist) {
        try {
            // 检查用户名是否已存在
            LambdaQueryWrapper<Psychologist> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Psychologist::getUsername, psychologist.getUsername());
            Psychologist existing = getOne(wrapper);
            
            if (existing != null) {
                return false; // 用户名已存在
            }
            
            // 设置默认值
            psychologist.setStatus("正常"); // 新注册专家状态为正常
            psychologist.setRating(BigDecimal.valueOf(4.5)); // 默认评分
            psychologist.setAccount(BigDecimal.ZERO); // 初始账户余额为0
            
            // 保存专家信息
            return save(psychologist);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean resetPassword(String username, String email, String newPassword) {
        try {
            // 验证用户名和邮箱是否匹配（只允许正常状态的专家重置密码）
            LambdaQueryWrapper<Psychologist> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Psychologist::getUsername, username)
                   .eq(Psychologist::getEmail, email)
                   .eq(Psychologist::getStatus, "正常");
            Psychologist psychologist = getOne(wrapper);
            
            if (psychologist == null) {
                return false; // 用户名和邮箱不匹配
            }
            
            // 更新密码
            Psychologist updatePsychologist = new Psychologist();
            updatePsychologist.setId(psychologist.getId());
            updatePsychologist.setPassword(newPassword);
            updatePsychologist.setUpdateTime(LocalDateTime.now());
            
            return updateById(updatePsychologist);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}