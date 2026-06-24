package com.easymall.service;

import com.easymall.entity.VO.HomeAggregateVO;

public interface HomeService {

    /**
     * 获取首页聚合数据（轮播、分类、推荐、秒杀、浏览历史）
     *
     * @param userId 用户ID（未登录传 null）
     * @return 首页聚合数据
     */
    HomeAggregateVO getHomeAggregate(Long userId);
}
