package com.maicorder.config;

import com.maicorder.filter.JwtAuthenticationEntryPoint;
import com.maicorder.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置类
 * 配置JWT认证和请求授权
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用CSRF（适用于REST API）
            .csrf(csrf -> csrf.disable())
            
            // 禁用会话管理（无状态认证）
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 配置请求授权 (cao默认为拦截!!!!)
            .authorizeHttpRequests(authorize -> authorize
//                    .requestMatchers("/api/get-apikey","/api/update-apikey").authenticated()
                    .anyRequest().permitAll()
            )
            
            // 配置无令牌时的处理
            .exceptionHandling(exception -> 
                exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            
            // 添加JWT过滤器
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
