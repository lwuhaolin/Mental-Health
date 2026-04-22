package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.ContactMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * 联系我们留言Mapper接口
 */
@Mapper
public interface ContactMessageMapper extends BaseMapper<ContactMessage> {
}