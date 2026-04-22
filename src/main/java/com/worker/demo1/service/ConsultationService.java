package com.worker.demo1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.worker.demo1.entity.Consultation;
import java.util.List;
import java.util.Map;

/**
 * 咨询服务接口
 */
public interface ConsultationService extends IService<Consultation> {
    
    /**
     * 用户预约咨询
     * @param consultation 咨询信息
     * @return 预约结果
     */
    boolean bookConsultation(Consultation consultation);
    
    /**
     * 专家接单
     * @param consultationId 咨询ID
     * @param psychologistId 专家ID
     * @return 接单结果
     */
    boolean acceptConsultation(Long consultationId, Long psychologistId);
    
    /**
     * 获取用户咨询记录
     * @param userId 用户ID
     * @return 咨询记录列表
     */
    List<Consultation> getUserConsultations(Long userId);
    
    /**
     * 获取专家咨询记录
     * @param psychologistId 专家ID
     * @return 咨询记录列表
     */
    List<Consultation> getPsychologistConsultations(Long psychologistId);
    
    /**
     * 完成咨询
     * @param consultationId 咨询ID
     * @param duration 咨询时长
     * @param rating 用户评分
     * @param feedback 用户反馈
     * @return 完成结果
     */
    boolean completeConsultation(Long consultationId, Integer duration, Integer rating, String feedback);
    
    /**
     * 获取专家的患者列表
     * @param psychologistId 专家ID
     * @return 患者列表
     */
    List<Map<String, Object>> getPsychologistPatients(Long psychologistId);

    /**
     * 根据用户ID获取咨询记录
     * @param userId 用户ID
     * @return 咨询记录列表
     */
    List<Consultation> getConsultationsByUserId(Long userId);

    /**
     * 根据ID获取咨询详情
     * @param id 咨询ID
     * @return 咨询详情
     */
    Consultation getById(Long id);

    /**
     * 更新咨询状态
     * @param consultationId 咨询ID
     * @param status 新状态
     * @return 更新结果
     */
    boolean updateConsultationStatus(Long consultationId, String status);
    
    /**
     * 获取收入趋势数据
     * @param psychologistId 专家ID
     * @param range 时间范围 (week/month/quarter)
     * @return 收入趋势数据
     */
    Map<String, Object> getIncomeTrend(Long psychologistId, String range);
    
    /**
     * 获取咨询类型分布
     * @param psychologistId 专家ID
     * @return 咨询类型分布数据
     */
    List<Map<String, Object>> getConsultationTypes(Long psychologistId);
    
    /**
     * 获取咨询时长统计
     * @param psychologistId 专家ID
     * @return 时长统计数据
     */
    List<Map<String, Object>> getDurationStats(Long psychologistId);
    
    /**
     * 获取评分分布
     * @param psychologistId 专家ID
     * @return 评分分布数据
     */
    List<Map<String, Object>> getRatingDistribution(Long psychologistId);
    
    /**
     * 获取详细统计报表
     * @param psychologistId 专家ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 详细统计数据
     */
    List<Map<String, Object>> getDetailedStats(Long psychologistId, String startDate, String endDate);
    
    /**
     * 提交咨询反馈
     * @param consultationId 咨询ID
     * @param rating 用户评分
     * @param feedback 用户反馈
     * @return 提交结果
     */
    boolean submitFeedback(Long consultationId, Integer rating, String feedback);
    
    /**
     * 获取用户咨询记录（包含专家信息）
     * @param userId 用户ID
     * @return 咨询记录列表（包含专家名称）
     */
    List<Map<String, Object>> getUserConsultationsWithPsychologist(Long userId);
}