package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.Assessment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测评问卷Mapper接口
 * 继承BaseMapper实现基本的CRUD操作
 */
@Mapper
public interface AssessmentMapper extends BaseMapper<Assessment> {
    // 可以在这里添加自定义的查询方法
}