package com.easymall.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 全局 CORS 过滤器，确保在最高优先级执行
 * 解决 WebMvcConfigurer.addCorsMappings() 在某些情况下不生效的问题
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String origin = request.getHeader("Origin");
        
        // 只允许配置的源
        if (origin != null && (
                "http://localhost:5174".equals(origin) ||
                "http://localhost:5173".equals(origin) ||
                "http://localhost:8080".equals(origin))) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, PATCH");
            // 注意：当 Allow-Credentials 为 true 时，Allow-Headers 不能使用 * 通配符
            // 必须明确列出所有允许的请求头
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, X-Requested-With, Accept, Origin, Cache-Control, X-Requested-With");
            response.setHeader("Access-Control-Expose-Headers", "Authorization, Content-Disposition");
            response.setHeader("Access-Control-Max-Age", "3600");
        }

        // 处理 OPTIONS 预检请求，直接返回 200
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(req, res);
    }
}
