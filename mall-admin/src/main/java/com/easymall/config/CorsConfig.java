package com.easymall.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 前端运行地址（Vue默认5173，以你浏览器地址栏为准）
                .allowedOrigins("http://localhost:5173","http://localhost:5174")
                // 允许所有请求方式
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // 放行所有请求头，保证Token可以携带
                .allowedHeaders("*")
                // 允许携带Cookie、凭证（对应前端withCredentials: true）
                .allowCredentials(true)
                // 预检请求有效期1小时
                .maxAge(3600);
    }
}