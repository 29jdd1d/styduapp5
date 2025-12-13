package com.studyapp.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 网关白名单配置属性
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "gateway")
public class AuthWhiteListProperties {

    /**
     * 白名单路径（不需要登录）
     */
    private List<String> whiteList;
}
