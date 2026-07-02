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

    // 本地上传目录路径（与 application.yml 中 file.upload.path 一致）
    private static final String UPLOAD_PATH = "E:/java-ffmpeg-upload/";
    // 前端访问前缀（需配合静态资源映射）
    private static final String IMAGE_URL_PREFIX = "/api/upload/";

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

        // 1.4 Entity 转 VO，并将本地文件路径转换为 HTTP URL
        List<CarouselVO> voList = carouselList.stream()
                .filter(entity -> entity.getImageUrl() != null && !entity.getImageUrl().trim().isEmpty())
                .map(entity -> {
                    CarouselVO vo = new CarouselVO();
                    BeanUtils.copyProperties(entity, vo);
                    // 将本地路径转为 HTTP URL
                    vo.setImageUrl(convertToHttpUrl(vo.getImageUrl()));
                    return vo;
                })
                .toList();

        // 1.5 写入 Redis 缓存，TTL 设为 1 小时（轮播图变更频率极低）
        redisComponent.setCarouselCache(JSON.toJSONString(voList), 1, TimeUnit.HOURS);
        log.info("轮播图数据已写入缓存，条数：{}", voList.size());

        return voList;
    }

    /**
     * 将本地文件路径转换为 HTTP URL
     * 例如: E:/java-ffmpeg-upload/banner.jpg -> /api/upload/banner.jpg
     */
    private String convertToHttpUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return imageUrl;
        }
        // 已经是 HTTP/HTTPS 链接，直接返回
        if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
            return imageUrl;
        }
        // 是本地文件路径，提取文件名拼接为 URL
        if (imageUrl.startsWith(UPLOAD_PATH)) {
            String fileName = imageUrl.substring(UPLOAD_PATH.length());
            return IMAGE_URL_PREFIX + fileName;
        }
        // 其他格式直接返回
        return imageUrl;
    }
}
