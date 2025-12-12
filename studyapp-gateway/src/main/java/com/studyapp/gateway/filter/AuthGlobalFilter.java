package com.studyapp.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.studyapp.gateway.config.GatewayProperties;
import com.studyapp.gateway.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局认证过滤器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private final GatewayProperties gatewayProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getPath().toString();

        // 检查是否是白名单路径
        if (isWhiteList(path)) {
            return chain.filter(exchange);
        }

        // 获取Token
        String token = getToken(request);
        if (token == null || token.isEmpty()) {
            return unauthorized(exchange, "未登录或Token已过期");
        }

        // 验证Token
        Claims claims = JwtUtils.parseToken(token);
        if (claims == null) {
            return unauthorized(exchange, "Token无效");
        }

        // 检查Token是否过期
        if (!JwtUtils.validateToken(token)) {
            return unauthorized(exchange, "Token已过期");
        }

        // 将用户信息添加到请求头
        ServerHttpRequest.Builder requestBuilder = request.mutate();

        // 判断是管理员还是普通用户
        if (JwtUtils.isAdminToken(token)) {
            Long adminId = JwtUtils.getAdminId(token);
            if (adminId != null) {
                requestBuilder.header("X-Admin-Id", String.valueOf(adminId));
            }
        } else {
            Long userId = JwtUtils.getUserId(token);
            if (userId != null) {
                requestBuilder.header("X-User-Id", String.valueOf(userId));
            }
        }

        ServerHttpRequest newRequest = requestBuilder.build();
        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    /**
     * 检查是否是白名单路径
     */
    private boolean isWhiteList(String path) {
        List<String> whiteList = gatewayProperties.getWhiteList();
        if (whiteList == null || whiteList.isEmpty()) {
            return false;
        }
        for (String pattern : whiteList) {
            if (pathMatcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从请求头获取Token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }
        return token;
    }

    /**
     * 返回未授权响应
     */
    private Mono<Void> unauthorized(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 1001);
        result.put("message", message);
        result.put("data", null);

        try {
            byte[] bytes = objectMapper.writeValueAsString(result).getBytes(StandardCharsets.UTF_8);
            DataBuffer buffer = response.bufferFactory().wrap(bytes);
            return response.writeWith(Mono.just(buffer));
        } catch (JsonProcessingException e) {
            log.error("JSON序列化失败", e);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return -100;
    }
}
