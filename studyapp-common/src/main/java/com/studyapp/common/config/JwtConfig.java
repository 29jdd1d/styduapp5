package com.studyapp.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置
 * 通过 Nacos 共享配置获取
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * 密钥（至少256位）
     */
    private String secret;

    /**
     * Token有效期（秒），默认7天
     */
    private Long expiration;

    /**
     * Token前缀
     */
    private String tokenPrefix;

    /**
     * 请求头名称
     */
    private String headerName = "Authorization";
}
