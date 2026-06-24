package com.easymall.controller;

import com.easymall.component.RedisComponent;
import com.easymall.entity.DTO.BrowseRecordDTO;
import com.easymall.entity.VO.BrowseHistoryVO;
import com.easymall.entity.constants.Constants;
import com.easymall.result.ApiResult;
import com.easymall.service.BrowseHistoryService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户浏览历史控制器
 */
@RestController
@RequestMapping("/browse-history")
@Slf4j
@Validated
public class BrowseHistoryController {

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @Autowired
    private RedisComponent redisComponent;

    /**
     * 记录浏览行为（用户点击商品详情页时调用）
     * POST /api/v1/browse-history/record
     */
    @PostMapping("/record")
    public ApiResult<String> recordBrowse(HttpServletRequest request,
                                          @Valid @RequestBody BrowseRecordDTO dto) {
        // 从请求头解析用户ID
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return ApiResult.error(401, "请先登录");
        }

        log.info("记录浏览历史，userId：{}，productId：{}", userId, dto.getProductId());
        browseHistoryService.recordBrowse(userId, dto.getProductId());
        return ApiResult.success("记录成功");
    }

    /**
     * 获取用户最近浏览历史（首页用）
     * GET /api/v1/browse-history/recent
     *
     * 说明：未登录用户返回空列表
     */
    @GetMapping("/recent")
    public ApiResult<List<BrowseHistoryVO>> getRecentHistory(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        log.info("获取浏览历史，userId：{}", userId);

        List<BrowseHistoryVO> list = browseHistoryService.getRecentHistory(userId);
        return ApiResult.success(list);
    }

    /**
     * 删除单条浏览记录
     * DELETE /api/v1/browse-history/{id}
     */
    @DeleteMapping("/{id}")
    public ApiResult<String> deleteById(@PathVariable Long id) {
        log.info("删除浏览记录，id：{}", id);
        browseHistoryService.deleteById(id);
        return ApiResult.success("删除成功");
    }

    /**
     * 清空用户所有浏览历史
     * DELETE /api/v1/browse-history/clear
     */
    @DeleteMapping("/clear")
    public ApiResult<String> clearAll(HttpServletRequest request) {
        Long userId = getUserIdFromToken(request);
        if (userId == null) {
            return ApiResult.error(401, "请先登录");
        }

        log.info("清空用户浏览历史，userId：{}", userId);
        browseHistoryService.clearByUserId(userId);
        return ApiResult.success("清空成功");
    }

    /**
     * 从请求头解析用户ID（示例方法）
     */
    /**
     * 从请求中获取用户ID
     * 方案：从 Cookie 中读取 RefreshToken，通过 Redis 反查用户ID
     */
    /**
     * 从请求中获取用户ID
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
            String userIdStr = (String) redisComponent.get(redisKey);
            if (!StringUtils.hasText(userIdStr)) {
                log.warn("RefreshToken 已过期或无效");
                return null;
            }

            return Long.parseLong(userIdStr);
        } catch (Exception e) {
            log.error("解析用户ID异常", e);
            return null;
        }
    }

    /**
     * 从 Cookie 数组中获取指定名称的 Cookie 值
     */
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        // 这里用的就是 HttpServletRequest 的 getCookies() 方法
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