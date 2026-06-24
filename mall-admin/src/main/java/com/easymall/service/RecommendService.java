package com.easymall.service;


import com.easymall.entity.constants.RecommendProduct;

public interface RecommendService {



    // 后台管理
    void saveRecommend(RecommendProduct entity);
    void updateRecommend(RecommendProduct entity);
    void deleteRecommend(Long id);

    // 手动清理缓存
    void clearRecommendCache();
}