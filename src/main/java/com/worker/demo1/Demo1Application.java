package com.worker.demo1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 星语航海心理测评与分析平台 - 主应用类
 */
@SpringBootApplication
public class Demo1Application {

    public static void main(String[] args) {
        SpringApplication.run(Demo1Application.class, args);
        System.out.println("==========================================");
        System.out.println("星语航海心理测评与分析平台启动成功！");
        System.out.println("访问地址: http://localhost:8080");
        System.out.println("数据库配置请查看: src/main/resources/application.properties");
        System.out.println("==========================================");
    }

}