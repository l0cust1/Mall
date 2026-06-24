package com.easymall.service;


import com.alibaba.fastjson.JSON;
import com.easymall.component.RedisComponent;
import com.easymall.entity.Carousel;
import com.easymall.entity.VO.CarouselVO;
import com.easymall.mapper.CarouselMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Autowired
    private RedisComponent redisComponent;

    // ======================== 1. 首页查询（带缓存） ========================
    @Override
    public List<CarouselVO> getHomeCarousels() {
        // 1.1 先查 Redis
        String cachedJson = redisComponent.getCarouselCache();
        if (cachedJson != null) {
            log.debug("轮播图缓存命中，直接返回");
            return JSON.parseArray(cachedJson, CarouselVO.class);
        }

        // 1.2 缓存未命中，查数据库
        log.info("轮播图缓存未命中，查询数据库...");
        List<Carousel> carouselList = carouselMapper.selectHomeList(6); // 最多6条

        // 1.3 空值处理：如果没有数据，返回空数组，并缓存空数组（短 TTL，防穿透）
        if (CollectionUtils.isEmpty(carouselList)) {
            log.warn("轮播图无数据，缓存空数组");
            redisComponent.setCarouselCache("[]", 5, TimeUnit.MINUTES);
            return Collections.emptyList();
        }

        // 1.4 Entity 转 VO
        List<CarouselVO> voList = carouselList.stream()
                .filter(entity -> entity.getImageUrl() != null && !entity.getImageUrl().trim().isEmpty())
                .map(entity -> {
                    CarouselVO vo = new CarouselVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .toList();

        // 1.5 写入 Redis 缓存，TTL 设为 1 小时（轮播图变更频率极低）
        redisComponent.setCarouselCache(JSON.toJSONString(voList), 1, TimeUnit.HOURS);
        log.info("轮播图数据已写入缓存，条数：{}", voList.size());

        return voList;
    }


}
