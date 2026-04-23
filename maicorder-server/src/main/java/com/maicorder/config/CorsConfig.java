package com.maicorder.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot 3.5.9 专用跨域配置类
 * 仅允许：http://localhost:8081、http://swan416.top、https://swan416.top
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    // 全局CORS过滤器（优先级最高，确保所有接口生效）
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 允许的源（用 allowedOriginPatterns 代替过时的 allowedOrigins）
        config.addAllowedOriginPattern("http://localhost:8081");
        config.addAllowedOriginPattern("http://swan416.top");
        config.addAllowedOriginPattern("https://swan416.top");

        // 2. 允许携带Token/Cookie（必须开启）
        config.setAllowCredentials(true);

        // 3. 允许所有请求方法
        config.addAllowedMethod("*");

        // 4. 允许所有请求头
        config.addAllowedHeader("*");

        // 5. 预检请求缓存1小时，提升性能
        config.setMaxAge(3600L);

        // 6. 对所有接口生效
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    // 补充WebMvc配置（双重保障，避免过滤器失效）
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns(
                        "http://localhost:8081",
                        "http://swan416.top",
                        "https://swan416.top"
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}