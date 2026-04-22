package com.worker.demo1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.worker.demo1.entity.AssessmentQuestion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 测评题目Mapper接口
 * 继承BaseMapper实现基本的CRUD操作
 */
@Mapper
public interface AssessmentQuestionMapper extends BaseMapper<AssessmentQuestion> {
    // 可以在这里添加自定义的查询方法
}