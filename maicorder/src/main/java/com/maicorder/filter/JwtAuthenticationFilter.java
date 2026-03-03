package com.maicorder.filter;

import com.maicorder.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 * 拦截所有请求，验证Authorization头中的令牌
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        // 获取Authorization头
        String authorizationHeader = request.getHeader("Authorization");
        
        // 验证令牌
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            
            try {
                // 解析令牌
                Claims claims = jwtUtils.parseToken(token);
                
                // 从令牌中获取用户信息
                Long userId = claims.get("userId", Long.class);
                String username = claims.get("username", String.class);
                
                if (userId != null && username != null) {
                    // 创建认证对象
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(
                                    userId, null, new ArrayList<>());
                    
                    // 设置认证信息到上下文
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // 令牌无效，清除认证信息
                SecurityContextHolder.clearContext();
            }
        }
        
        // 继续处理请求
        chain.doFilter(request, response);
    }
}
