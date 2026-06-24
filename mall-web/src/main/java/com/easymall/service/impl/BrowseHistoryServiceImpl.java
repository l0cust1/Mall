package com.easymall.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.easymall.component.RedisComponent;
import com.easymall.entity.BrowseHistory;
import com.easymall.entity.VO.BrowseHistoryVO;
import com.easymall.entity.constants.ProductBase;
import com.easymall.mapper.BrowseHistoryMapper;
import com.easymall.mapper.ProductBaseMapper;
import com.easymall.service.BrowseHistoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.easymall.entity.constants.Constants.HOME_AGGREGATE_PREFIX;

@Service
@Slf4j
public class BrowseHistoryServiceImpl implements BrowseHistoryService {

    @Autowired
    private BrowseHistoryMapper browseHistoryMapper;

    @Autowired
    private ProductBaseMapper productBaseMapper;  // 用于查询商品名称/图片/价格

    @Autowired
    private RedisComponent redisComponent;



    private static final Long CACHE_TTL = 10L;  // 10分钟

    // ==================== 1. 记录浏览行为 ====================
    @Override
    @Transactional
    public void recordBrowse(Long userId, Long productId) {
        if (userId == null || userId <= 0) {
            log.warn("用户未登录，不记录浏览历史");
            return;
        }

        // 1. 查询商品信息（从 product_base 获取）
        ProductBase product = productBaseMapper.selectById(productId);
        if (product == null) {
            log.warn("商品不存在，productId：{}", productId);
            return;
        }

        // 2. 先删除该用户对该商品的旧浏览记录（保证每个用户对同一商品只有一条最新记录）
        browseHistoryMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BrowseHistory>()
                        .eq(BrowseHistory::getUserId, userId)
                        .eq(BrowseHistory::getProductId, productId)
        );

        // 3. 插入新记录
        BrowseHistory entity = new BrowseHistory();
        entity.setUserId(userId);
        entity.setProductId(productId);
        entity.setProductName(product.getProductName());
        entity.setProductImage(product.getCover());
        entity.setPrice(product.getMinPrice());
        entity.setBrowseTime(LocalDateTime.now());

        browseHistoryMapper.insert(entity);
        log.info("记录浏览历史成功，userId：{}，productId：{}", userId, productId);

        // 4. 清除该用户的首页缓存（因为首页包含浏览历史）
        clearCache(userId);
    }

    // ==================== 2. 查询最近浏览（首页用） ====================
    @Override
    public List<BrowseHistoryVO> getRecentHistory(Long userId) {
        // 未登录直接返回空列表
        if (userId == null || userId <= 0) {
            log.debug("用户未登录，返回空浏览历史");
            return Collections.emptyList();
        }

        // 1. 查缓存
        String cachedJson = redisComponent.getHistoryCache(userId);
        if (StringUtils.hasText(cachedJson)) {
            log.debug("浏览历史缓存命中，userId：{}", userId);
            return JSON.parseArray(cachedJson, BrowseHistoryVO.class);
        }

        // 2. 缓存未命中，查数据库
        log.info("浏览历史缓存未命中，查询数据库，userId：{}", userId);
        List<BrowseHistory> list = browseHistoryMapper.selectRecentByUserId(userId, 10);

        // 3. 空值处理
        if (CollectionUtils.isEmpty(list)) {
            // 缓存空数组，TTL 5分钟（防止缓存穿透）
            redisComponent.setHistoryCache(userId, "[]", 5, TimeUnit.MINUTES);
            return Collections.emptyList();
        }

        // 4. Entity 转 VO（时间转时间戳）
        List<BrowseHistoryVO> voList = list.stream()
                .map(entity -> {
                    BrowseHistoryVO vo = new BrowseHistoryVO();
                    BeanUtils.copyProperties(entity, vo);
                    // LocalDateTime 转时间戳（毫秒）
                    vo.setBrowseTime(entity.getBrowseTime()
                            .atZone(ZoneId.systemDefault())
                            .toInstant()
                            .toEpochMilli());
                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 写入缓存（TTL 10分钟）
        redisComponent.setHistoryCache(userId, JSON.toJSONString(voList), CACHE_TTL, TimeUnit.MINUTES);
        log.info("浏览历史已写入缓存，userId：{}，条数：{}", userId, voList.size());

        return voList;
    }

    // ==================== 3. 后台管理方法 ====================
    @Override
    public BrowseHistory getById(Long id) {
        return browseHistoryMapper.selectById(id);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        BrowseHistory entity = browseHistoryMapper.selectById(id);
        if (entity != null) {
            browseHistoryMapper.deleteById(id);
            clearCache(entity.getUserId());
            log.info("删除浏览记录成功，id：{}", id);
        }
    }

    @Override
    @Transactional
    public void clearByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return;
        }
        browseHistoryMapper.delete(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<BrowseHistory>()
                        .eq(BrowseHistory::getUserId, userId)
        );
        clearCache(userId);
        log.info("清空用户浏览历史成功，userId：{}", userId);
    }

    // ==================== 4. 缓存清理 ====================
    @Override
    public void clearCache(Long userId) {
        if (userId != null && userId > 0) {
            // 删除该用户的浏览历史缓存
            redisComponent.deleteHistoryCache(userId);
        }

        // 删除所有首页聚合缓存（因为首页包含浏览历史）
        Set<String> keys = redisComponent.scanKeys(HOME_AGGREGATE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisComponent.deleteKeys(keys.toArray(new String[0]));
            log.info("已清除所有首页聚合缓存，共 {} 个 Key", keys.size());
        }
        log.info("浏览历史缓存清理完毕，userId：{}", userId);
    }
}