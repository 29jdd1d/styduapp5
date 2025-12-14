package com.studyapp.gateway.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Gateway 配置类
 */
@Configuration
public class GatewayConfig {

    /**
     * ObjectMapper Bean
     * 用于 JSON 序列化和反序列化
     */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
