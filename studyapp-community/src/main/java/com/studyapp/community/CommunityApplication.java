package com.studyapp.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 社区服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.studyapp")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.studyapp.community.feign")
@MapperScan("com.studyapp.community.mapper")
public class CommunityApplication {

    public static void main(String[] args) {
        SpringApplication.run(CommunityApplication.class, args);
    }
}
