package com.studyapp.practice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 刷题服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.studyapp")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.studyapp.practice.feign")
@MapperScan("com.studyapp.practice.mapper")
public class PracticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PracticeApplication.class, args);
    }
}
