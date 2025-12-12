package com.studyapp.common.config;

import com.studyapp.common.utils.CosUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 腾讯云COS配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "cos")
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
