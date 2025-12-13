package com.studyapp.common.config;

import com.studyapp.common.utils.CosUtils;
import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS配置
 * 通过 Nacos 共享配置获取
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cos")
@ConditionalOnProperty(prefix = "cos", name = "secret-id")
public class CosConfig {

    /**
     * SecretId
     */
    private String secretId;

    /**
     * SecretKey
     */
    private String secretKey;

    /**
     * 地域
     */
    private String region;

    /**
     * 存储桶名称
     */
    private String bucketName;

    @Bean
    public CosUtils cosUtils() {
        return new CosUtils(secretId, secretKey, region, bucketName);
    }
}
