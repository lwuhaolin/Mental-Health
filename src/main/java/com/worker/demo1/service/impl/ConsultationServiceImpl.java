package com.worker.demo1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.worker.demo1.entity.Consultation;
import com.worker.demo1.entity.User;
import com.worker.demo1.entity.Psychologist;
import com.worker.demo1.mapper.ConsultationMapper;
import com.worker.demo1.mapper.PsychologistMapper;
import com.worker.demo1.service.ConsultationService;
import com.worker.demo1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.math.BigDecimal;

/**
 * 咨询服务实现类
 */
@Service
public class ConsultationServiceImpl extends ServiceImpl<ConsultationMapper, Consultation> implements ConsultationService {
    
    @Autowired
    private ConsultationMapper consultationMapper;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private PsychologistMapper psychologistMapper;
    
    @Override
    public boolean bookConsultation(Consultation consultation) {
        consultation.setStatus("待接单");
        consultation.setCreateTime(LocalDateTime.now());
        consultation.setUpdateTime(LocalDateTime.now());
        return consultationMapper.insert(consultation) > 0;
    }
    
    @Override
    public boolean acceptConsultation(Long consultationId, Long psychologistId) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation != null && "待接单".equals(consultation.getStatus())) {
            consultation.setPsychologistId(psychologistId);
            consultation.setStatus("进行中");
            consultation.setStartTime(LocalDateTime.now());
            consultation.setUpdateTime(LocalDateTime.now());
            return consultationMapper.updateById(consultation) > 0;
        }
        return false;
    }
    
    @Override
    public List<Consultation> getUserConsultations(Long userId) {
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        return consultationMapper.selectList(queryWrapper);
    }
    
    @Override
    public List<Consultation> getPsychologistConsultations(Long psychologistId) {
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("psychologist_id", psychologistId)
                   .orderByDesc("create_time");
        return consultationMapper.selectList(queryWrapper);
    }
    
    @Override
    public boolean completeConsultation(Long consultationId, Integer duration, Integer rating, String feedback) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation != null && "进行中".equals(consultation.getStatus())) {
            consultation.setStatus("已完成");
            consultation.setEndTime(LocalDateTime.now());
            
            // 计算实际咨询时长和费用
            if (consultation.getStartTime() != null && consultation.getEndTime() != null) {
                // 计算实际时长（秒）
                long durationSeconds = java.time.Duration.between(
                    consultation.getStartTime(), consultation.getEndTime()).getSeconds();
                
                // 转换为分钟，不足一分钟按一分钟计算（向上取整）
                long durationMinutes = (long) Math.ceil(durationSeconds / 60.0);
                
                consultation.setDuration((int) durationMinutes);
                
                // 获取专家的hourlyRate并计算费用
                if (consultation.getPsychologistId() != null) {
                    com.worker.demo1.entity.Psychologist psychologist = psychologistMapper.selectById(consultation.getPsychologistId());
                    if (psychologist != null && psychologist.getHourlyRate() != null) {
                        double hourlyRate = psychologist.getHourlyRate().doubleValue();
                        double calculatedCost = calculateConsultationIncome(consultation, hourlyRate);
                        consultation.setCost(java.math.BigDecimal.valueOf(calculatedCost));
                        
                        System.out.println("完成咨询 - 咨询ID: " + consultationId + 
                                         ", 时长: " + durationMinutes + "分钟" +
                                         ", 时薪: " + hourlyRate + 
                                         ", 费用: " + calculatedCost);
                    }
                }
            } else {
                // 如果没有时间信息，使用传入的duration参数
                consultation.setDuration(duration);
            }
            
            consultation.setUserRating(rating);
            consultation.setUserFeedback(feedback);
            consultation.setUpdateTime(LocalDateTime.now());
            
            System.out.println("准备更新咨询记录，咨询ID: " + consultationId + 
                             ", 新状态: " + consultation.getStatus() +
                             ", 费用: " + consultation.getCost());
            
            // 先保存咨询记录（包含计算好的cost）
            boolean updateResult = consultationMapper.updateById(consultation) > 0;
            
            if (updateResult) {
                System.out.println("咨询记录更新成功，开始更新专家总收入");
                
                // 强制刷新以确保数据已写入数据库
                try {
                    Thread.sleep(100); // 等待100ms确保数据库写入完成
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                
                // 再更新专家的总收入（重新计算所有已完成咨询的总收入）
                updatePsychologistTotalIncome(consultation.getPsychologistId());
            } else {
                System.out.println("咨询记录更新失败");
            }
            
            return updateResult;
        }
        return false;
    }
    
    @Override
    public List<Map<String, Object>> getPsychologistPatients(Long psychologistId) {
        List<Map<String, Object>> patients = new ArrayList<>();
        
        // 获取该专家的所有咨询记录，按创建时间降序排列（显示所有记录）
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("psychologist_id", psychologistId)
                   .orderByDesc("create_time");
        List<Consultation> consultations = consultationMapper.selectList(queryWrapper);
        
        System.out.println("专家ID " + psychologistId + " 的咨询记录数量: " + consultations.size());
        
        // 返回所有咨询记录，不再按用户分组
        for (Consultation consultation : consultations) {
            Long userId = consultation.getUserId();
            
            Map<String, Object> patientInfo = new HashMap<>();
            patientInfo.put("id", userId);
            patientInfo.put("consultationId", consultation.getId());
            
            // 从用户表获取真实姓名和年龄
            User user = userService.getById(userId);
            
            // 优先显示真实姓名，其次显示用户名，最后显示患者ID
            String displayName = "患者" + userId;
            if (user != null) {
                if (user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
                    displayName = user.getRealName();
                } else if (user.getUsername() != null && !user.getUsername().trim().isEmpty()) {
                    displayName = user.getUsername();
                }
            }
            
            Integer userAge = user != null ? user.getAge() : null;
            patientInfo.put("name", displayName);
            patientInfo.put("age", userAge != null ? userAge : 25);
            
            // 使用咨询记录的创建时间
            patientInfo.put("createTime", consultation.getCreateTime());
            patientInfo.put("lastConsultation", consultation.getCreateTime());
            
            // 添加预约时间、开始时间、结束时间
            patientInfo.put("scheduledTime", consultation.getScheduledTime());
            patientInfo.put("startTime", consultation.getStartTime());
            patientInfo.put("endTime", consultation.getEndTime());
            
            // 状态和其他信息
            patientInfo.put("status", consultation.getStatus());
            patientInfo.put("consultationCount", 1); // 每条记录计为1次
            patientInfo.put("problem", consultation.getTitle());
            
            // 添加用户反馈和评分
            patientInfo.put("userFeedback", consultation.getUserFeedback());
            patientInfo.put("userRating", consultation.getUserRating());
            patientInfo.put("description", consultation.getDescription());
            
            System.out.println("构建咨询记录 - 咨询ID: " + consultation.getId() + 
                             ", 用户: " + patientInfo.get("name") + 
                             ", 状态: " + consultation.getStatus() +
                             ", 创建时间: " + consultation.getCreateTime());
            
            patients.add(patientInfo);
        }
        
        System.out.println("返回咨询记录总数: " + patients.size());
        return patients;
    }

    @Override
    public Consultation getById(Long id) {
        return consultationMapper.selectById(id);
    }

    @Override
    public List<Consultation> getConsultationsByUserId(Long userId) {
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        return consultationMapper.selectList(queryWrapper);
    }

    @Override
    public boolean updateConsultationStatus(Long consultationId, String status) {
        Consultation consultation = consultationMapper.selectById(consultationId);
        if (consultation != null) {
            // 调试输出接收到的状态值
            System.out.println("接收到的状态值: '" + status + "'");
            
            // 处理编码问题 - 直接支持URL编码的中文状态值
            String normalizedStatus = status.trim();
            
            // 检查是否是URL编码的中文状态值
            if ("%E5%B7%B2%E5%AE%8C%E6%88%90".equals(status)) {
                normalizedStatus = "已完成";
                System.out.println("检测到URL编码状态值，转换为: '" + normalizedStatus + "'");
            } else if ("%E8%BF%9B%E8%A1%8C%E4%B8%AD".equals(status)) {
                normalizedStatus = "进行中";
                System.out.println("检测到URL编码状态值，转换为: '" + normalizedStatus + "'");
            } else if ("%E5%BE%85%E6%8E%A5%E5%8D%95".equals(status)) {
                normalizedStatus = "待接单";
                System.out.println("检测到URL编码状态值，转换为: '" + normalizedStatus + "'");
            } else if (status.contains("?")) {
                try {
                    // 尝试从ISO-8859-1编码转换为UTF-8
                    String utf8Status = new String(status.getBytes("ISO-8859-1"), "UTF-8");
                    System.out.println("UTF-8解码后状态值: '" + utf8Status + "'");
                    normalizedStatus = utf8Status;
                } catch (Exception e) {
                    System.out.println("编码转换失败: " + e.getMessage());
                }
            }
            
            // 检查是否是有效的中文状态值
            if ("待接单".equals(normalizedStatus) || "进行中".equals(normalizedStatus) || "已完成".equals(normalizedStatus)) {
                consultation.setStatus(normalizedStatus);
                consultation.setUpdateTime(LocalDateTime.now());
                
                // 如果状态变为"已完成"，设置结束时间
                if ("已完成".equals(normalizedStatus)) {
                    consultation.setEndTime(LocalDateTime.now());
                    
                    // 计算时长和费用
                    if (consultation.getStartTime() != null) {
                        // 计算实际时长（秒）
                        long durationSeconds = java.time.Duration.between(
                            consultation.getStartTime(), consultation.getEndTime()).getSeconds();
                        
                        // 转换为分钟，不足一分钟按一分钟计算（向上取整）
                        long durationMinutes = (long) Math.ceil(durationSeconds / 60.0);
                        consultation.setDuration((int) durationMinutes);
                        
                        // 计算费用
                        Psychologist psychologist = psychologistMapper.selectById(consultation.getPsychologistId());
                        if (psychologist != null && psychologist.getHourlyRate() != null) {
                            double calculatedCost = calculateConsultationIncome(consultation, psychologist.getHourlyRate().doubleValue());
                            consultation.setCost(java.math.BigDecimal.valueOf(calculatedCost));
                            
                            System.out.println("更新咨询状态为已完成 - 咨询ID: " + consultationId + 
                                             ", 时长: " + durationMinutes + "分钟" +
                                             ", 费用: " + calculatedCost);
                        }
                    }
                }
                
                // 先更新咨询记录
                boolean updateResult = consultationMapper.updateById(consultation) > 0;
                
                // 如果更新成功且状态为已完成，则更新专家总收入
                if (updateResult && "已完成".equals(normalizedStatus)) {
                    System.out.println("咨询状态已更新，开始更新专家总收入");
                    
                    // 等待确保数据库写入完成
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    
                    // 更新专家的总收入
                    updatePsychologistTotalIncome(consultation.getPsychologistId());
                }
                
                return updateResult;
            } else {
                System.out.println("无效的状态值: '" + status + "'，允许的状态值: 待接单, 进行中, 已完成");
                return false;
            }
        }
        return false;
    }
    
    /**
     * 更新专家的总收入（重新计算所有已完成咨询的总收入）
     * @param psychologistId 专家ID
     */
    private void updatePsychologistTotalIncome(Long psychologistId) {
        if (psychologistId == null) return;
        
        System.out.println("========== 开始更新专家ID " + psychologistId + " 的总收入 ==========");
        
        // 直接查询已完成状态的咨询记录
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("psychologist_id", psychologistId)
                   .eq("status", "已完成");
        
        List<Consultation> completedConsultations = consultationMapper.selectList(queryWrapper);
        System.out.println("查询到的已完成咨询数量: " + completedConsultations.size());
        
        // 获取专家信息以获取时薪
        Psychologist psychologist = psychologistMapper.selectById(psychologistId);
        if (psychologist == null) {
            System.out.println("未找到专家ID: " + psychologistId);
            return;
        }
        
        System.out.println("专家当前账户余额: " + (psychologist.getAccount() != null ? psychologist.getAccount() : "0"));
        
        // 计算总收入
        double totalIncome = 0.0;
        for (Consultation c : completedConsultations) {
            double income = 0.0;
            
            // 优先使用已保存的 cost 字段
            if (c.getCost() != null && c.getCost().doubleValue() > 0) {
                income = c.getCost().doubleValue();
                System.out.println("  ✓ 咨询ID " + c.getId() + " 使用已保存费用: ¥" + income);
            } else if (c.getStartTime() != null && c.getEndTime() != null && psychologist.getHourlyRate() != null) {
                // 如果 cost 为空，重新计算
                income = calculateConsultationIncome(c, psychologist.getHourlyRate().doubleValue());
                System.out.println("  ⚠ 咨询ID " + c.getId() + " 重新计算费用: ¥" + income);
            } else {
                System.out.println("  ✗ 咨询ID " + c.getId() + " 无法计算费用（缺少时间或时薪信息）");
            }
            
            totalIncome += income;
        }
        
        System.out.println("计算出的总收入: ¥" + totalIncome);
        
        // 更新专家的 account 字段
        psychologist.setAccount(BigDecimal.valueOf(totalIncome));
        int updateResult = psychologistMapper.updateById(psychologist);
        
        if (updateResult > 0) {
            System.out.println("✓ 成功更新专家账户，新余额: ¥" + totalIncome);
        } else {
            System.out.println("✗ 更新专家账户失败");
        }
        
        System.out.println("========== 更新完成 ==========");
    }
    
    @Override
    public Map<String, Object> getIncomeTrend(Long psychologistId, String range) {
        Map<String, Object> result = new HashMap<>();
        List<String> dates = new ArrayList<>();
        List<Double> incomes = new ArrayList<>();
        
        // 根据range确定天数
        int days = switch (range) {
            case "week" -> 7;
            case "quarter" -> 90;
            default -> 30; // month
        };
        
        // 获取该专家的所有已完成咨询
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("psychologist_id", psychologistId)
                   .eq("status", "已完成")
                   .orderByDesc("end_time");
        List<Consultation> consultations = consultationMapper.selectList(queryWrapper);
        
        // 获取专家时薪
        Psychologist psychologist = psychologistMapper.selectById(psychologistId);
        double hourlyRate = psychologist != null && psychologist.getHourlyRate() != null ? 
            psychologist.getHourlyRate().doubleValue() : 300.0;
        
        // 按日期统计收入
        Map<String, Double> dailyIncome = new HashMap<>();
        for (Consultation consultation : consultations) {
            if (consultation.getEndTime() != null) {
                String date = consultation.getEndTime().toLocalDate().toString();
                double income = calculateConsultationIncome(consultation, hourlyRate);
                dailyIncome.put(date, dailyIncome.getOrDefault(date, 0.0) + income);
            }
        }
        
        // 生成最近N天的数据
        LocalDateTime now = LocalDateTime.now();
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime date = now.minusDays(i);
            String dateStr = date.toLocalDate().toString();
            dates.add(dateStr);
            incomes.add(dailyIncome.getOrDefault(dateStr, 0.0));
        }
        
        result.put("dates", dates);
        result.put("incomes", incomes);
        return result;
    }
    
    @Override
    public List<Map<String, Object>> getConsultationTypes(Long psychologistId) {
        List<Map<String, Object>> typeData = new ArrayList<>();
        
        // 获取该专家的所有咨询
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("psychologist_id", psychologistId);
        List<Consultation> consultations = consultationMapper.selectList(queryWrapper);
        
        // 按咨询类型统计
        Map<String, Integer> typeCount = new HashMap<>();
        for (Consultation consultation : consultations) {
            String type = consultation.getConsultationType();
            if (type == null || type.trim().isEmpty()) {
                type = "其他";
            }
            typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
        }
        
        // 转换为列表格式
        for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", entry.getKey());
            item.put("count", entry.getValue());
            typeData.add(item);
        }
        
        return typeData;
    }
    
    @Override
    public List<Map<String, Object>> getDurationStats(Long psychologistId) {
        List<Map<String, Object>> durationData = new ArrayList<>();
        
        // 获取该专家的所有已完成咨询
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("psychologist_id", psychologistId)
                   .eq("status", "已完成");
        List<Consultation> consultations = consultationMapper.selectList(queryWrapper);
        
        // 按时长区间统计
        int under30 = 0, between30And60 = 0, between60And90 = 0, over90 = 0;
        
        for (Consultation consultation : consultations) {
            int duration = consultation.getDuration() != null ? consultation.getDuration() : 60;
            if (duration < 30) {
                under30++;
            } else if (duration < 60) {
                between30And60++;
            } else if (duration < 90) {
                between60And90++;
            } else {
                over90++;
            }
        }
        
        // 组装数据
        addDurationRange(durationData, "30分钟以下", under30);
        addDurationRange(durationData, "30-60分钟", between30And60);
        addDurationRange(durationData, "60-90分钟", between60And90);
        addDurationRange(durationData, "90分钟以上", over90);
        
        return durationData;
    }
    
    @Override
    public List<Map<String, Object>> getRatingDistribution(Long psychologistId) {
        List<Map<String, Object>> ratingData = new ArrayList<>();
        
        // 获取该专家的所有已完成咨询
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("psychologist_id", psychologistId)
                   .eq("status", "已完成")
                   .isNotNull("user_rating");
        List<Consultation> consultations = consultationMapper.selectList(queryWrapper);
        
        // 按评分统计
        int[] ratingCount = new int[6]; // 0-5星
        for (Consultation consultation : consultations) {
            Integer rating = consultation.getUserRating();
            if (rating != null && rating >= 1 && rating <= 5) {
                ratingCount[rating]++;
            }
        }
        
        // 组装数据（从5星到1星）
        for (int i = 5; i >= 1; i--) {
            Map<String, Object> item = new HashMap<>();
            item.put("rating", i);
            item.put("count", ratingCount[i]);
            ratingData.add(item);
        }
        
        return ratingData;
    }
    
    @Override
    public List<Map<String, Object>> getDetailedStats(Long psychologistId, String startDate, String endDate) {
        List<Map<String, Object>> detailedData = new ArrayList<>();
        
        // 获取该专家的咨询记录
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("psychologist_id", psychologistId)
                   .orderByDesc("create_time");
        List<Consultation> consultations = consultationMapper.selectList(queryWrapper);
        
        // 获取专家时薪
        Psychologist psychologist = psychologistMapper.selectById(psychologistId);
        double hourlyRate = psychologist != null && psychologist.getHourlyRate() != null ? 
            psychologist.getHourlyRate().doubleValue() : 300.0;
        
        // 按日期统计
        Map<String, DailyStats> dailyStatsMap = new HashMap<>();
        
        for (Consultation consultation : consultations) {
            if (consultation.getCreateTime() != null) {
                String date = consultation.getCreateTime().toLocalDate().toString();
                
                DailyStats stats = dailyStatsMap.computeIfAbsent(date, k -> new DailyStats());
                stats.consultationCount++;
                
                if ("已完成".equals(consultation.getStatus())) {
                    int duration = consultation.getDuration() != null ? consultation.getDuration() : 60;
                    stats.totalDuration += duration;
                    stats.income += calculateConsultationIncome(consultation, hourlyRate);
                    
                    if (consultation.getUserRating() != null) {
                        stats.totalRating += consultation.getUserRating();
                        stats.ratingCount++;
                    }
                }
            }
        }
        
        // 转换为列表
        for (Map.Entry<String, DailyStats> entry : dailyStatsMap.entrySet()) {
            Map<String, Object> item = new HashMap<>();
            DailyStats stats = entry.getValue();
            
            item.put("date", entry.getKey());
            item.put("consultationCount", stats.consultationCount);
            item.put("duration", stats.totalDuration);
            item.put("income", Math.round(stats.income * 100) / 100.0);
            item.put("avgRating", stats.ratingCount > 0 ? 
                Math.round((stats.totalRating / stats.ratingCount) * 10) / 10.0 : 0);
            item.put("patientCount", stats.consultationCount); // 简化处理
            
            detailedData.add(item);
        }
        
        // 按日期降序排序
        detailedData.sort((a, b) -> ((String)b.get("date")).compareTo((String)a.get("date")));
        
        return detailedData;
    }
    
    // 辅助方法：计算单次咨询收入（根据开始时间和结束时间，以60分钟为计费单位）
    private double calculateConsultationIncome(Consultation consultation, double hourlyRate) {
        if (consultation.getStartTime() != null && consultation.getEndTime() != null) {
            // 计算实际时长（秒）
            long durationSeconds = java.time.Duration.between(
                consultation.getStartTime(), consultation.getEndTime()).getSeconds();
            
            // 转换为分钟，不足一分钟按一分钟计算（向上取整）
            long durationMinutes = (long) Math.ceil(durationSeconds / 60.0);
            
            // 验证时长合理性（必须大于0且小于24小时）
            if (durationMinutes > 0 && durationMinutes <= 1440) {
                // 转换为小时，向上取整到1小时（60分钟）为单位
                double durationHours = Math.ceil(durationMinutes / 60.0);
                return durationHours * hourlyRate;
            }
        }
        
        // 如果没有有效的时间，返回0（不计算收入）
        return 0.0;
    }
    
    // 辅助方法：添加时长区间数据
    private void addDurationRange(List<Map<String, Object>> list, String range, int count) {
        Map<String, Object> item = new HashMap<>();
        item.put("range", range);
        item.put("count", count);
        list.add(item);
    }
    
    // 内部类：日统计数据
    private static class DailyStats {
        int consultationCount = 0;
        int totalDuration = 0;
        double income = 0.0;
        int totalRating = 0;
        int ratingCount = 0;
    }
    
    /**
     * 提交咨询反馈
     */
    @Override
    public boolean submitFeedback(Long consultationId, Integer rating, String feedback) {
        try {
            Consultation consultation = this.getById(consultationId);
            if (consultation != null) {
                consultation.setUserRating(rating);
                consultation.setUserFeedback(feedback);
                return this.updateById(consultation);
            }
            return false;
        } catch (Exception e) {
            System.err.println("提交反馈失败: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 获取用户咨询记录（包含专家信息）
     */
    @Override
    public List<Map<String, Object>> getUserConsultationsWithPsychologist(Long userId) {
        List<Map<String, Object>> result = new ArrayList<>();
        
        // 获取用户的所有咨询记录
        QueryWrapper<Consultation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId)
                   .orderByDesc("create_time");
        List<Consultation> consultations = consultationMapper.selectList(queryWrapper);
        
        // 为每条咨询记录添加专家信息
        for (Consultation consultation : consultations) {
            Map<String, Object> consultationMap = new HashMap<>();
            consultationMap.put("id", consultation.getId());
            consultationMap.put("title", consultation.getTitle());
            consultationMap.put("description", consultation.getDescription());
            consultationMap.put("consultationType", consultation.getConsultationType());
            consultationMap.put("status", consultation.getStatus());
            consultationMap.put("scheduledTime", consultation.getScheduledTime());
            consultationMap.put("startTime", consultation.getStartTime());
            consultationMap.put("endTime", consultation.getEndTime());
            consultationMap.put("duration", consultation.getDuration());
            consultationMap.put("cost", consultation.getCost());
            consultationMap.put("userRating", consultation.getUserRating());
            consultationMap.put("userFeedback", consultation.getUserFeedback());
            consultationMap.put("createTime", consultation.getCreateTime());
            
            // 获取专家信息
            if (consultation.getPsychologistId() != null) {
                com.worker.demo1.entity.Psychologist psychologist = psychologistMapper.selectById(consultation.getPsychologistId());
                if (psychologist != null) {
                    consultationMap.put("psychologistName", psychologist.getRealName());
                } else {
                    consultationMap.put("psychologistName", "未分配");
                }
            } else {
                consultationMap.put("psychologistName", "待分配");
            }
            
            result.add(consultationMap);
        }
        
        return result;
    }
}