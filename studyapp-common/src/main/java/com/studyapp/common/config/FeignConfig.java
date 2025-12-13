package com.studyapp.common.config;

import com.studyapp.common.constant.CommonConstants;
import com.studyapp.common.utils.UserContext;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign配置
 * 仅在Feign类存在时加载
 */
@Configuration
@ConditionalOnClass(name = "feign.RequestInterceptor")
public class FeignConfig {

    /**
     * Feign请求拦截器，传递用户信息
     */
    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 传递用户ID
                Long userId = UserContext.getUserId();
                if (userId != null) {
                    template.header(CommonConstants.HEADER_USER_ID, String.valueOf(userId));
                }

                // 传递管理员ID
                Long adminId = UserContext.getAdminId();
                if (adminId != null) {
                    template.header(CommonConstants.HEADER_ADMIN_ID, String.valueOf(adminId));
                }
            }
        };
    }
}
