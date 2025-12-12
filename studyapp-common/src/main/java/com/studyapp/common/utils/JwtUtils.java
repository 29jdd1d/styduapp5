package com.studyapp.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtils {

    /**
     * 密钥（生产环境应从配置中读取）
     */
    private static final String SECRET = "studyapp-secret-key-must-be-at-least-256-bits-long";

    /**
     * Token有效期（7天）
     */
    private static final long EXPIRATION = 7 * 24 * 60 * 60 * 1000L;

    /**
     * Token前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 请求头名称
     */
    public static final String HEADER_NAME = "Authorization";

    private static SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成Token
     *
     * @param userId 用户ID
     * @param openid 微信openid
     * @return Token
     */
    public static String generateToken(Long userId, String openid) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("openid", openid);

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
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
    public static String generateAdminToken(Long adminId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", adminId);
        claims.put("username", username);
        claims.put("type", "admin");

        return Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(adminId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSecretKey())
                .compact();
    }

    /**
     * 解析Token
     *
     * @param token Token
     * @return Claims
     */
    public static Claims parseToken(String token) {
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
     *
     * @param token Token
     * @return 管理员ID
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
     *
     * @param token Token
     * @return 是否有效
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
     *
     * @param token Token
     * @return 是否是管理员
     */
    public static boolean isAdminToken(String token) {
        Claims claims = parseToken(token);
        if (claims != null) {
            return "admin".equals(claims.get("type"));
        }
        return false;
    }
}
