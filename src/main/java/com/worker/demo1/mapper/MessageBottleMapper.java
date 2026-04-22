package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.MessageBottle;
import org.apache.ibatis.annotations.Mapper;

/**
 * 漂流瓶Mapper接口
 */
@Mapper
public interface MessageBottleMapper extends BaseMapper<MessageBottle> {
}