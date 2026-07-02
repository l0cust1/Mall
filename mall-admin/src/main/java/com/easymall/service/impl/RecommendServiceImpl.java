package com.easymall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.constants.RecommendProduct;
import com.easymall.mapper.RecommendProductMapper;
import com.easymall.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.easymall.entity.constants.Constants.HOME_AGGREGATE_PREFIX;

@Service
public class RecommendServiceImpl extends ServiceImpl<RecommendProductMapper,RecommendProduct> implements RecommendService {
    @Autowired
    private RecommendProductMapper recommendMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    @Transactional
    public void saveRecommend(RecommendProduct entity) {
        recommendMapper.insert(entity);
        clearRecommendCache();

    }

    @Override
    @Transactional
    public void updateRecommend(RecommendProduct entity) {
        recommendMapper.updateById(entity);
        clearRecommendCache();

    }

    @Override
    @Transactional
    public void deleteRecommend(Long id) {
        recommendMapper.deleteById(id);
        clearRecommendCache();

    }

    @Override
    public void clearRecommendCache() {

        // 删除所有首页聚合缓存
        Set<String> keys = redisTemplate.keys(HOME_AGGREGATE_PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);

        }

        // 同时删除推荐商品独立缓存（mall-web 端使用）
        redisTemplate.delete("recommend:home:hot");

    }
}
