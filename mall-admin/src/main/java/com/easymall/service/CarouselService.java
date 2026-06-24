package com.easymall.service;


import com.easymall.entity.Carousel;

public interface CarouselService {



    /**
     * 后台新增轮播图（自动清除缓存）
     */
    void saveCarousel(Carousel carousel);

    /**
     * 后台更新轮播图（自动清除缓存）
     */
    void updateCarousel(Carousel carousel);

    /**
     * 后台删除轮播图（自动清除缓存）
     */
    void deleteCarousel(Long id);

    /**
     * 手动清除轮播图相关缓存（供后台系统调用）
     */
    void clearCarouselCache();
}