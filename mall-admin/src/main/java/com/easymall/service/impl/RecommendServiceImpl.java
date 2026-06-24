package com.easymall.service.impl;

import com.easymall.entity.constants.RecommendProduct;
import com.easymall.mapper.RecommendProductMapper;
import com.easymall.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.easymall.entity.constants.Constants.HOME_AGGREGATE_PREFIX;

public class RecommendServiceImpl implements RecommendService {
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

    }
}
