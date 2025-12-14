package com.studyapp.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 认证白名单配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "gateway")
public class AuthWhiteListProperties {
    
    /**
     * 白名单路径列表
     */
    private List<String> whiteList;
}
