package com.studyapp.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 网关服务启动类
 */
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(
    basePackages = {"com.studyapp.gateway", "com.studyapp.common"},
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = com.studyapp.common.exception.GlobalExceptionHandler.class
    )
)
public class GatewayApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
