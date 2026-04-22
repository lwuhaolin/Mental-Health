package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.Psychologist;
import org.apache.ibatis.annotations.Mapper;

/**
 * 心理专家Mapper接口
 */
@Mapper
public interface PsychologistMapper extends BaseMapper<Psychologist> {
}