package com.studyapp.gateway.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类（网关专用）
 * 配置从Nacos共享配置获取
 */
@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret:studyapp-secret-key-must-be-at-least-256-bits-long}")
    private String secret;

    // 静态实例
    private static JwtUtils instance;

    @PostConstruct
    public void init() {
        instance = this;
        log.info("JwtUtils initialized with secret length: {}", secret.length());
    }

    private static SecretKey getSecretKey() {
        String secretKey = instance != null ? instance.secret 
                : "studyapp-secret-key-must-be-at-least-256-bits-long";
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 解析Token
     */
    public static Claims parseToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return Jwts.parser()
                    .verifyWith(getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("Token解析失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Object userId = claims.get("userId");
            if (userId != null) {
                return Long.valueOf(userId.toString());
            }
        }
        return null;
    }

    /**
     * 获取管理员ID
     */
    public static Long getAdminId(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            Object adminId = claims.get("adminId");
            if (adminId != null) {
                return Long.valueOf(adminId.toString());
            }
        }
        return null;
    }

    /**
     * 验证Token是否有效
     */
    public static boolean validateToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return false;
        }
        return claims.getExpiration().after(new Date());
    }

    /**
     * 判断是否是管理员Token
     */
    public static boolean isAdminToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return "admin".equals(claims.get("type"));
        }
        return false;
    }
}
