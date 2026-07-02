package com.easymall.controller;

import com.easymall.entity.VO.HomeAggregateVO;
import com.easymall.entity.constants.Constants;
import com.easymall.result.ApiResult;
import com.easymall.service.HomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页控制器
 */
@Tag(name = "首页模块", description = "首页聚合数据相关接口")
@RestController
@RequestMapping("/home")
@Slf4j
public class HomeController {

    @Autowired
    private HomeService homeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取首页聚合数据
     * GET /api/v1/home/aggregate
     *
     * 说明：未登录用户也可访问，浏览历史未登录时返回空列表
     */
    @Operation(summary = "获取首页聚合数据", description = "一次性返回轮播图、分类、推荐、秒杀、浏览历史等首页所需全部数据。未登录用户也可访问，浏览历史未登录时返回空列表。")
    @GetMapping("/aggregate")
    public ApiResult<HomeAggregateVO> getHomeAggregate(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        log.info("获取首页聚合数据，userId：{}", userId);

        HomeAggregateVO vo = homeService.getHomeAggregate(userId);
        return ApiResult.success(vo);
    }

    /**
     * 从请求中获取用户ID
     * 方案：从 Cookie 中读取 RefreshToken，通过 Redis 反查用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            // 1. 从 Cookie 中获取 RefreshToken
            String refreshToken = getCookieValue(request, Constants.COOKIE_REFRESH_TOKEN);
            if (!StringUtils.hasText(refreshToken)) {
                log.debug("未找到 RefreshToken Cookie，用户未登录");
                return null;
            }

            // 2. 从 Redis 中通过 RefreshToken 查询用户ID
            String redisKey = Constants.REDIS_KEY_REFRESH_TOKEN + refreshToken;
            String userIdStr = stringRedisTemplate.opsForValue().get(redisKey);
            if (!StringUtils.hasText(userIdStr)) {
                log.warn("RefreshToken 已过期或无效");
                return null;
            }

            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            log.error("解析用户ID失败，Redis中的值不是合法数字: {}", e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("解析用户ID异常: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从 Cookie 数组中获取指定名称的 Cookie 值
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
