package com.easymall.controller;


import com.easymall.entity.Carousel;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.CarouselService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/carousel")
public class CarouselController extends ABaseController {

    @Resource
    private CarouselService carouselService;
    /**
     * 新增轮播图
     */
    @PostMapping("/add")
    public ResponseVO add(@RequestBody Carousel carousel) {
        try {
            if (carousel.getTitle() == null || carousel.getTitle().trim().isEmpty()) {
                return getFailResponseVO("轮播图标题不能为空");
            }
            if (carousel.getImageUrl() == null || carousel.getImageUrl().trim().isEmpty()) {
                return getFailResponseVO("轮播图图片不能为空");
            }
            carouselService.saveCarousel(carousel);
            log.info("新增轮播图成功");
            return getSuccessResponseVO(null);
        } catch (Exception e) {
            log.error("新增轮播图失败: {}", e.getMessage(), e);
            return getFailResponseVO("新增轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 更新轮播图
     */
    @PostMapping("/update")
    public ResponseVO update(@RequestBody Carousel carousel) {
        try {
            if (carousel.getId() == null) {
                return getFailResponseVO("轮播图ID不能为空");
            }
            carouselService.updateCarousel(carousel);
            log.info("更新轮播图成功，ID: {}", carousel.getId());
            return getSuccessResponseVO(null);
        } catch (Exception e) {
            log.error("更新轮播图失败: {}", e.getMessage(), e);
            return getFailResponseVO("更新轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 删除轮播图
     */
    @PostMapping("/delete/{id}")
    public ResponseVO delete(@PathVariable Long id) {
        try {
            if (id == null) {
                return getFailResponseVO("轮播图ID不能为空");
            }
            carouselService.deleteCarousel(id);
            log.info("删除轮播图成功，ID: {}", id);
            return getSuccessResponseVO(null);
        } catch (Exception e) {
            log.error("删除轮播图失败: {}", e.getMessage(), e);
            return getFailResponseVO("删除轮播图失败: " + e.getMessage());
        }
    }

    /**
     * 手动清理轮播图缓存
     */
    @PostMapping("/clearCache")
    public ResponseVO clearCache() {
        try {
            carouselService.clearCarouselCache();
            log.info("轮播图缓存清理成功");
            return getSuccessResponseVO(null);
        } catch (Exception e) {
            log.error("清理轮播图缓存失败: {}", e.getMessage(), e);
            return getFailResponseVO("清理轮播图缓存失败: " + e.getMessage());
        }
    }
}
