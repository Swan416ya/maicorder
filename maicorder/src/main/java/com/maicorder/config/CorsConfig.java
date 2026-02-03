package com.maicorder.config; // 必须是你的包名，不能改！

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot 3.5.9 专用跨域配置类（解决前端CORS拦截问题）
 */
@Configuration // 必须加这个注解，让Spring识别为配置类
public class CorsConfig implements WebMvcConfigurer {

    // 全局CORS过滤器（优先级最高，确保所有/api接口都能跨域）
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 1. 允许前端的地址访问（填你前端实际运行的端口，比如8081/8889）
        config.addAllowedOrigin("http://localhost:8081"); // 优先用这个
        config.addAllowedOrigin("http://localhost:8889"); // 如果你之前用8889也加上
        config.setAllowCredentials(true); // 必须开，允许传Token/Cookie

        // 2. 允许所有请求方法（GET/POST等）
        config.addAllowedMethod("*");

        // 3. 允许所有请求头（比如Content-Type、Authorization）
        config.addAllowedHeader("*");

        // 4. 预检请求缓存1小时，减少OPTIONS请求
        config.setMaxAge(3600L);

        // 5. 配置所有接口生效（/** 代表所有路径）
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }

    // 补充WebMvc的CORS配置（双重保障，防止过滤器没生效）
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 只对/api开头的接口生效
                .allowedOrigins("http://localhost:8081", "http://localhost:8889")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}