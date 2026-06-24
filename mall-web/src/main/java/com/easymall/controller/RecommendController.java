package com.easymall.controller;

import com.easymall.entity.VO.RecommendProductVO;
import com.easymall.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 推荐商品控制器（首页推荐）
 */
@RestController
@RequestMapping("/recommend")

@Slf4j
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取首页热门推荐商品
     * 前端路径：GET /api/v1/recommend/hot
     */
    @GetMapping("/hot")
    public List<RecommendProductVO> getHotRecommends() {
        log.info("收到获取热门推荐商品请求");
        return recommendService.getHotRecommends();
    }
}