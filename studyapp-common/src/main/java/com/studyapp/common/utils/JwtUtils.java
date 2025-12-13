package com.studyapp.common.utils;

import com.studyapp.common.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 * 配置从Nacos获取
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtils {

    private final JwtConfig jwtConfig;

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 请求头名称
     */
    public static final String HEADER_NAME = "Authorization";

    // 静态实例，用于静态方法访问
    private static JwtUtils instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    private long getExpirationMs() {
        return jwtConfig.getExpiration() * 1000L;
    }

    /**
     * 生成Token
     *
     * @param userId 用户ID
     * @param openid 微信openid
     * @return Token
     */
    public String generateToken(Long userId, String openid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("openid", openid);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + getExpirationMs()))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 生成管理员Token
     *
     * @param adminId  管理员ID
     * @param username 用户名
     * @return Token
     */
    public String generateAdminToken(Long adminId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("username", username);
        claims.put("type", "admin");

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(adminId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + getExpirationMs()))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析Token
     *
     * @param token Token
     * @return Claims
     */
    public Claims parseToken(String token) {
        try {
            if (token.startsWith(TOKEN_PREFIX)) {
                token = token.substring(TOKEN_PREFIX.length());
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
     *
     * @param token Token
     * @return 用户ID
     */
    public Long getUserId(String token) {
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
     *
     * @param token Token
     * @return 管理员ID
     */
    public Long getAdminId(String token) {
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
     *
     * @param token Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            return false;
        }
        return claims.getExpiration().after(new Date());
    }

    /**
     * 判断是否是管理员Token
     *
     * @param token Token
     * @return 是否是管理员
     */
    public boolean isAdminToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return "admin".equals(claims.get("type"));
        }
        return false;
    }

    // ============ 静态方法（供Gateway等无法注入的场景使用） ============

    public static Long getUserIdStatic(String token) {
        return instance != null ? instance.getUserId(token) : null;
    }

    public static Long getAdminIdStatic(String token) {
        return instance != null ? instance.getAdminId(token) : null;
    }

    public static boolean validateTokenStatic(String token) {
        return instance != null && instance.validateToken(token);
    }

    public static boolean isAdminTokenStatic(String token) {
        return instance != null && instance.isAdminToken(token);
    }

    public static Claims parseTokenStatic(String token) {
        return instance != null ? instance.parseToken(token) : null;
    }
}
