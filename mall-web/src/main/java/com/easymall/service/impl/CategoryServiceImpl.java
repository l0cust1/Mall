package com.easymall.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.SysCategory;
import com.easymall.entity.VO.CategoryVO;
import com.easymall.mapper.SysCategoryMapper;
import com.easymall.service.SysCategoryService;
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
public class CategoryServiceImpl extends ServiceImpl<SysCategoryMapper, SysCategory> implements SysCategoryService {

    @Autowired
    private SysCategoryMapper categoryMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String CACHE_KEY_CATEGORY = "category:home:firstLevel";
    private static final String HOME_AGGREGATE_PREFIX = "home:aggregate:";

    @Override
    public List<CategoryVO> getFirstLevelCategories() {
        // 1. 查缓存
        String cachedJson = redisTemplate.opsForValue().get(CACHE_KEY_CATEGORY);
        if (StringUtils.hasText(cachedJson)) {
            log.debug("分类缓存命中");
            return JSON.parseArray(cachedJson, CategoryVO.class);
        }

        // 2. 缓存未命中，查数据库
        log.info("分类缓存未命中，查询数据库...");
        List<SysCategory> list = categoryMapper.selectFirstLevel(10);

        // 3. 空值处理
        if (CollectionUtils.isEmpty(list)) {
            log.warn("一级分类无数据");
            redisTemplate.opsForValue().set(CACHE_KEY_CATEGORY, "[]", 5, java.util.concurrent.TimeUnit.MINUTES);
            return Collections.emptyList();
        }

        // 4. Entity 转 VO
        List<CategoryVO> voList = list.stream()
                .filter(entity -> StringUtils.hasText(entity.getCategoryName()))
                .map(entity -> {
                    CategoryVO vo = new CategoryVO();
                    BeanUtils.copyProperties(entity, vo);
                    return vo;
                })
                .collect(Collectors.toList());

        // 5. 写入缓存
        redisTemplate.opsForValue().set(CACHE_KEY_CATEGORY, JSON.toJSONString(voList), 1, java.util.concurrent.TimeUnit.HOURS);
        log.info("分类数据已写入缓存，条数：{}", voList.size());

        return voList;
    }

}