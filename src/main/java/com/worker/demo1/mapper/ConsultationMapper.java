package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.Consultation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 咨询记录Mapper接口
 */
@Mapper
public interface ConsultationMapper extends BaseMapper<Consultation> {
}