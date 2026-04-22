package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.AiInteraction;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI互动记录Mapper接口
 */
@Mapper
public interface AiInteractionMapper extends BaseMapper<AiInteraction> {
}