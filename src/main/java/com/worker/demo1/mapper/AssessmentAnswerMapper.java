package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.AssessmentAnswer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测评答案Mapper接口
 */
@Mapper
public interface AssessmentAnswerMapper extends BaseMapper<AssessmentAnswer> {
}