package com.easymall.service;

import com.easymall.entity.VO.RecommendProductVO;

import java.util.List;

public interface RecommendService  {

    /**
     * 获取首页热门推荐商品（带缓存）
     */
    List<RecommendProductVO> getHotRecommends();


}