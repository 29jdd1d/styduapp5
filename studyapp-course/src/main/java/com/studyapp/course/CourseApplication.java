package com.studyapp.course;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 课程服务启动类
 */
@SpringBootApplication(scanBasePackages = "com.studyapp")
@EnableDiscoveryClient
@MapperScan("com.studyapp.course.mapper")
public class CourseApplication {

    public static void main(String[] args) {
        SpringApplication.run(CourseApplication.class, args);
    }
}
