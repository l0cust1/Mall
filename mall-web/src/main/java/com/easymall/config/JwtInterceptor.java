package com.easymall.config;

import com.easymall.entity.SysUser;
import com.easymall.service.SysUserService;
import com.easymall.utils.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SysUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 OPTIONS 预检请求
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(401);
            response.getWriter().write("未登录或token无效");
            return false;
        }

        String accessToken = authHeader.substring(7);
        if (!jwtUtil.validateToken(accessToken)) {
            response.setStatus(401);
            response.getWriter().write("token无效或已过期");
            return false;
        }

        // 从 JWT 中获取 userId（AccessToken 中携带了 userId claim）
        String userId = jwtUtil.getUserIdFromToken(accessToken);
        if (userId == null) {
            response.setStatus(401);
            response.getWriter().write("token无效");
            return false;
        }

        SysUser user = userService.getById(userId);
        if (user == null) {
            response.setStatus(401);
            response.getWriter().write("用户不存在");
            return false;
        }

        // 将用户信息存入请求域，供后续使用
        request.setAttribute("currentUser", user);
        return true;
    }
}