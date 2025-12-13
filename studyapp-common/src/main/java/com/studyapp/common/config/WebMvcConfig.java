package com.studyapp.common.config;

import com.studyapp.common.interceptor.UserInfoInterceptor;
import com.studyapp.common.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置
 * 仅在WebMvc类存在时加载
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.web.servlet.config.annotation.WebMvcConfigurer")
public class WebMvcConfig implements WebMvcConfigurer {

    // JwtUtils 为可选依赖，未配置时为null
    @Autowired(required = false)
    private JwtUtils jwtUtils;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInfoInterceptor(jwtUtils))
                .addPathPatterns("/**");
    }
}
