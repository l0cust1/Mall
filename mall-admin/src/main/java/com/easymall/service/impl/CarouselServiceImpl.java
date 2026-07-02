package com.easymall.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.easymall.component.RedisComponent;
import com.easymall.entity.Carousel;
import com.easymall.mapper.CarouselMapper;
import com.easymall.service.CarouselService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class CarouselServiceImpl implements CarouselService {

    @Autowired
    private CarouselMapper carouselMapper;

    @Autowired
    private RedisComponent redisComponent;



    // ======================== 2. 后台增删改（带缓存清理） ========================
    @Override
    @Transactional
    public void saveCarousel(Carousel carousel) {
        carouselMapper.insert(carousel);
        redisComponent.clearCarouselAllCache();
        log.info("新增轮播图成功，ID：{}", carousel.getId());
    }

    @Override
    @Transactional
    public void updateCarousel(Carousel carousel) {
        carouselMapper.updateById(carousel);
        redisComponent.clearCarouselAllCache();
        log.info("更新轮播图成功，ID：{}", carousel.getId());
    }

    @Override
    @Transactional
    public void deleteCarousel(Long id) {
        carouselMapper.deleteById(id);
        redisComponent.clearCarouselAllCache();
        log.info("删除轮播图成功，ID：{}", id);
    }

    // ======================== 3. 手动清理缓存（核心） ========================
    @Override
    public void clearCarouselCache() {
        redisComponent.clearCarouselAllCache();
        log.info("轮播图缓存清理完毕");
    }

    // ======================== 4. 列表查询 ========================
    @Override
    public List<Carousel> listAllCarousels() {
        LambdaQueryWrapper<Carousel> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Carousel::getSortOrder);
        return carouselMapper.selectList(wrapper);
    }
}
