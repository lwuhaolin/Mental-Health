package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.UserAssessment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户测评记录Mapper接口
 */
@Mapper
public interface UserAssessmentMapper extends BaseMapper<UserAssessment> {
}