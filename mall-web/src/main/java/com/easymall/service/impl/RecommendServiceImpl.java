package com.easymall.service.impl;

import com.alibaba.fastjson2.JSON;
import com.easymall.entity.RecommendProduct;
import com.easymall.entity.VO.RecommendProductVO;
import com.easymall.mapper.RecommendProductMapper;
import com.easymall.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class RecommendServiceImpl implements RecommendService {

    @Autowired
    private RecommendProductMapper recommendMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CACHE_KEY_RECOMMEND = "recommend:home:hot";
    private static final String HOME_AGGREGATE_PREFIX = "home:aggregate:";

    @Override
    public List<RecommendProductVO> getHotRecommends() {
        // 1. 查缓存
        String cachedJson = redisTemplate.opsForValue().get(CACHE_KEY_RECOMMEND);
        if (StringUtils.hasText(cachedJson)) {
            log.debug("推荐商品缓存命中");
            return JSON.parseArray(cachedJson, RecommendProductVO.class);
        }

        // 2. 缓存未命中，查数据库（推荐类型 1 = 热门推荐）
        log.info("推荐商品缓存未命中，查询数据库...");
        List<RecommendProduct> list = recommendMapper.selectByType(1, 20); // 限制20条

        // 3. 空值处理
        if (CollectionUtils.isEmpty(list)) {
            log.warn("热门推荐无数据");
            redisTemplate.opsForValue().set(CACHE_KEY_RECOMMEND, "[]", 5, java.util.concurrent.TimeUnit.MINUTES);
            return Collections.emptyList();
        }

        // 4. Entity 转 VO
        List<RecommendProductVO> voList = list.stream()
                .filter(entity -> StringUtils.hasText(entity.getProductName()))
                .map(entity -> {
                    RecommendProductVO vo = new RecommendProductVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 写入缓存（TTL 10分钟，推荐商品变更频率中等）
        redisTemplate.opsForValue().set(CACHE_KEY_RECOMMEND, JSON.toJSONString(voList), 10, java.util.concurrent.TimeUnit.MINUTES);
        log.info("推荐商品已写入缓存，条数：{}", voList.size());

        return voList;
    }


}