package com.easymall.service;


import com.easymall.entity.VO.CarouselVO;

import java.util.List;

public interface CarouselService {

    /**
     * 首页获取轮播图（带 Redis 缓存）
     */
    List<CarouselVO> getHomeCarousels();


}