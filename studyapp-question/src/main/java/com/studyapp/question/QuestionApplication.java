package com.studyapp.question;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 题库服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.studyapp")
@EnableDiscoveryClient
@MapperScan("com.studyapp.question.mapper")
public class QuestionApplication {

    public static void main(String[] args) {
        SpringApplication.run(QuestionApplication.class, args);
    }
}
