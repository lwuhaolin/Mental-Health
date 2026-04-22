package com.worker.demo1.config;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis Plus配置类
 */
@Configuration
@MapperScan("com.worker.demo1.mapper")
public class MybatisPlusConfig {
    
    /**
     * MyBatis Plus拦截器配置
     * 暂时不配置分页插件，先确保应用能正常启动
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        return new MybatisPlusInterceptor();
    }
}