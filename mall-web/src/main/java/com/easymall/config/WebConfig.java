package com.easymall.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Value("${file.upload.path:E:/java-ffmpeg-upload/}")
    private String uploadPath;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**", "/home/**", "/products/**",
                        // SpringDoc / Swagger UI 放行
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs",
                        "/webjars/**",
                        // 静态资源放行
                        "/upload/**",

                        "/categoryV1/first-level",
                        "/categoryV1/getSubCategoryWithProduct",

                        // 支付宝异步回调（无登录 Token）
                        "/pay/callback"

                );
    }

    /**
     * 静态资源映射：将 /api/upload/ 映射到本地文件目录
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String path = new File(uploadPath).toURI().toString();
        registry.addResourceHandler("/upload/**")
                .addResourceLocations(path);
    }

    /**
     * 使用 WebMvcConfigurer 方式配置 CORS（推荐，与 Spring Boot 3.x 兼容）
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5174", "http://localhost:5173", "http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // 注意：当 allowCredentials 为 true 时，allowedHeaders 不能使用 * 通配符
                .allowedHeaders("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin", "Cache-Control")
                .exposedHeaders("Authorization", "Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
    }
}