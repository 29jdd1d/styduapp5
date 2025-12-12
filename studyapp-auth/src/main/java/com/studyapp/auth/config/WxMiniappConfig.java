package com.studyapp.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 微信小程序配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxMiniappConfig {

    /**
     * AppID
     */
    private String appId;

    /**
     * AppSecret
     */
    private String appSecret;
}
