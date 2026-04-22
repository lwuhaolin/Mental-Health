package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.ConsultationMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 专家咨询留言Mapper
 */
@Mapper
public interface ConsultationMessageMapper extends BaseMapper<ConsultationMessage> {
}