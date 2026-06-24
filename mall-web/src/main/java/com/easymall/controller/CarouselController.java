package com.easymall.controller;


import com.easymall.entity.VO.CarouselVO;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.CarouselService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/carousel")
public class CarouselController extends ABaseController {

    @Resource
    private CarouselService carouselService;

    /**
     * 首页获取轮播图列表（带缓存）
     */
    @GetMapping("/list")
    public ResponseVO<List<CarouselVO>> list() {
        try {
            List<CarouselVO> voList = carouselService.getHomeCarousels();
            return getSuccessResponseVO(voList);
        } catch (Exception e) {
            log.error("获取轮播图列表失败: {}", e.getMessage(), e);
            return getFailResponseVO("获取轮播图列表失败: " + e.getMessage());
        }
    }


}
