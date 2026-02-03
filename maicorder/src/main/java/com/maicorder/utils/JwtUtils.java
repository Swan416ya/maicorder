package com.maicorder.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * JWT工具类（RESTful无状态认证核心）
 */
@Component
public class JwtUtils {
    // 密钥（建议放配置文件，长度至少32位）
    @Value("${jwt.secret:maicorder_arcade_2024_secret_key_32bit}")
    private String secret;

    // Token过期时间：2小时（单位：毫秒）
    @Value("${jwt.expire:7200000}")
    private Long expire;

    // 生成Token
    public String generateToken(Long userId, String username) {
        // 生成加密密钥
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        // 构建Token
        return Jwts.builder()
                // 自定义载荷（存储用户关键信息，不要存密码）
                .claim("userId", userId)
                .claim("username", username)
                // 签发时间
                .setIssuedAt(new Date())
                // 过期时间
                .setExpiration(new Date(System.currentTimeMillis() + expire))
                // 签名加密
                .signWith(key)
                .compact();
    }

    // 解析Token（获取载荷信息）
    public Claims parseToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}