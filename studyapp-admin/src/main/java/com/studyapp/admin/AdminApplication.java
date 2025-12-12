package com.studyapp.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 管理后台服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.studyapp")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.studyapp.admin.feign")
@MapperScan("com.studyapp.admin.mapper")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
