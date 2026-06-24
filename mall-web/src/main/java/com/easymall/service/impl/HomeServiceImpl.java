package com.easymall.service.impl;

import com.alibaba.fastjson.JSON;
import com.easymall.component.RedisComponent;
import com.easymall.entity.VO.*;
import com.easymall.service.BrowseHistoryService;
import com.easymall.service.HomeService;
import com.easymall.service.SysCategoryService;
import com.easymall.service.CarouselService;
import com.easymall.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static com.easymall.entity.constants.Constants.HOME_AGGREGATE_PREFIX;

@Service
@Slf4j
public class HomeServiceImpl implements HomeService {

    @Autowired
    private CarouselService carouselService;

    @Autowired
    private SysCategoryService categoryService;

    @Autowired
    private RecommendService recommendService;

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @Autowired
    private RedisComponent redisComponent;

    /** 首页聚合缓存 TTL：300 秒（5 分钟） */
    private static final long CACHE_TTL = 300L;

    @Override
    public HomeAggregateVO getHomeAggregate(Long userId) {
        // 1. 构建缓存 key（未登录用户用 anonymous）
        String cacheKey = HOME_AGGREGATE_PREFIX + (userId != null && userId > 0 ? userId : "anonymous");

        // 2. 先查缓存
        String cachedJson = (String) redisComponent.get(cacheKey);
        if (cachedJson != null && !cachedJson.isEmpty()) {
            log.debug("首页聚合缓存命中，key：{}", cacheKey);
            return JSON.parseObject(cachedJson, HomeAggregateVO.class);
        }

        log.info("首页聚合缓存未命中，开始并行查询各模块数据");

        // 3. 缓存未命中，并行查询各模块
        CompletableFuture<List<CarouselVO>> carouselFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return carouselService.getHomeCarousels();
            } catch (Exception e) {
                log.error("查询轮播图异常", e);
                return Collections.emptyList();
            }
        });

        CompletableFuture<List<CategoryVO>> categoryFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return categoryService.getFirstLevelCategories();
            } catch (Exception e) {
                log.error("查询分类异常", e);
                return Collections.emptyList();
            }
        });

        CompletableFuture<List<RecommendProductVO>> recommendFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return recommendService.getHotRecommends();
            } catch (Exception e) {
                log.error("查询推荐商品异常", e);
                return Collections.emptyList();
            }
        });

        CompletableFuture<SeckillVO> seckillFuture = CompletableFuture.supplyAsync(() -> {
            try {
                // SeckillService 暂未实现，返回空对象
                return new SeckillVO();
            } catch (Exception e) {
                log.error("查询秒杀活动异常", e);
                return new SeckillVO();
            }
        });

        CompletableFuture<List<BrowseHistoryVO>> browseHistoryFuture = CompletableFuture.supplyAsync(() -> {
            try {
                // 未登录用户直接返回空列表，不走数据库查询
                if (userId == null || userId <= 0) {
                    return Collections.emptyList();
                }
                return browseHistoryService.getRecentHistory(userId);
            } catch (Exception e) {
                log.error("查询浏览历史异常", e);
                return Collections.emptyList();
            }
        });

        // 4. 等待所有查询完成
        CompletableFuture.allOf(carouselFuture, categoryFuture, recommendFuture, seckillFuture, browseHistoryFuture).join();

        // 5. 组装结果
        HomeAggregateVO vo = new HomeAggregateVO();
        vo.setCarousels(carouselFuture.join());
        vo.setCategories(categoryFuture.join());
        vo.setRecommendProducts(recommendFuture.join());
        vo.setSeckill(seckillFuture.join());
        vo.setBrowseHistory(browseHistoryFuture.join());

        // 6. 写入缓存
        redisComponent.set(cacheKey, JSON.toJSONString(vo), CACHE_TTL, TimeUnit.SECONDS);
        log.info("首页聚合数据已写入缓存，key：{}", cacheKey);

        return vo;
    }
}
