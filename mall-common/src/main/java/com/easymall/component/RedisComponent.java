package com.easymall.component;

import com.easymall.entity.constants.Constants;
import com.easymall.utils.RedisUtils;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.easymall.entity.constants.Constants.*;

@Component
public class RedisComponent {
    @Resource
    private RedisUtils redisUtils;

    /**
     * 通用获取值方法
     */
    public Object get(String key) {
        return redisUtils.get(key);
    }

    /**
     * 通用设置值方法
     *
     * @param key     缓存key
     * @param value   缓存值
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisUtils.set(key, value, timeout, unit);
    }

    public String saveCheckCode(String code) {
        String checkCodeKey = UUID.randomUUID().toString();
        redisUtils.set(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey, code, 60 * 10);
        return checkCodeKey;
    }

    public String getCheckCodeKey(String checkCodeKey) {
        return (String) redisUtils.get(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public String saveTokenInfo(String account) {
        String token = UUID.randomUUID().toString();
        redisUtils.set(Constants.REDIS_KEY_TOKEN_ADMIN + token, account, REDIS_KEY_EXPIRES_DAY);
        return token;
    }

    public void clearCheckCode(@NotEmpty String checkCodeKey) {
        redisUtils.del(Constants.REDIS_KEY_CHECK_CODE + checkCodeKey);
    }

    public void cleanTokenInfo(String token) {
        redisUtils.del(Constants.REDIS_KEY_TOKEN_ADMIN + token);
    }

    public void savePhoneCode(String phone, String code) {
        redisUtils.set(Constants.REDIS_KEY_PHONECHECK_CODE + phone, code, 60 * 10);
    }

    public String getPhoneCode(String phone) {
        return (String) redisUtils.get(Constants.REDIS_KEY_PHONECHECK_CODE + phone);
    }

    public void deletePhoneCode(String phone) {
        redisUtils.del(Constants.REDIS_KEY_PHONECHECK_CODE + phone);
    }

    // ======================== 轮播图缓存 ========================

    /**
     * 获取轮播图缓存（返回 JSON 字符串）
     *
     * @return 缓存命中返回 JSON 字符串；缓存不存在返回 null
     */
    public String getCarouselCache() {
        String json = (String) redisUtils.get(CACHE_KEY_CAROUSEL);
        if (!StringUtils.hasText(json)) {
            return null;
        }
        return json;
    }

    /**
     * 设置轮播图缓存
     *
     * @param json    JSON 字符串
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void setCarouselCache(String json, long timeout, TimeUnit unit) {
        redisUtils.set(CACHE_KEY_CAROUSEL, json, timeout, unit);
    }

    /**
     * 清理轮播图相关所有缓存（含首页聚合缓存）
     */
    public void clearCarouselAllCache() {
        redisUtils.del(CACHE_KEY_CAROUSEL);

        Set<String> keys = redisUtils.scanKeys(HOME_AGGREGATE_PREFIX + "*");
        if (!CollectionUtils.isEmpty(keys)) {
            redisUtils.del(keys.toArray(new String[0]));
        }
    }

    // ======================== 浏览历史缓存 ========================

    /**
     * 获取浏览历史缓存
     *
     * @param userId 用户ID
     * @return 缓存命中返回 JSON 字符串；缓存不存在返回 null
     */
    public String getHistoryCache(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;
        String json = (String) redisUtils.get(key);
        if (!StringUtils.hasText(json)) {
            return null;
        }
        return json;
    }

    /**
     * 设置浏览历史缓存
     *
     * @param userId  用户ID
     * @param json    JSON 字符串
     * @param timeout 过期时间
     * @param unit    时间单位
     */
    public void setHistoryCache(Long userId, String json, long timeout, TimeUnit unit) {
        String key = CACHE_KEY_PREFIX + userId;
        redisUtils.set(key, json, timeout, unit);
    }

    /**
     * 删除浏览历史缓存
     *
     * @param userId 用户ID
     */
    public void deleteHistoryCache(Long userId) {
        String key = CACHE_KEY_PREFIX + userId;
        redisUtils.del(key);
    }

    /**
     * 批量删除缓存（支持通配符模式）
     *
     * @param keys 要删除的key数组
     */
    public void deleteKeys(String... keys) {
        if (keys != null && keys.length > 0) {
            redisUtils.del(keys);
        }
    }

    /**
     * 模糊匹配 key（委托给 RedisUtils）
     *
     * @param pattern 匹配模式
     * @return 匹配的key集合
     */
    public Set<String> scanKeys(String pattern) {
        return redisUtils.scanKeys(pattern);
    }
}
