package com.easymall.controller;


import com.easymall.entity.Carousel;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.CarouselService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "轮播图管理", description = "后台管理轮播图的增删改及缓存清理")
@RestController
@RequestMapping("/admin/carousel")
public class CarouselController extends ABaseController {

    @Resource
    private CarouselService carouselService;

    /**
     * 新增轮播图
     */
    @Operation(summary = "新增轮播图", description = "新增一条首页轮播图记录，需提供标题和图片URL。新增成功后自动清除轮播图缓存。")
    @PostMapping("/add")
    public ResponseVO add(@Parameter(description = "轮播图实体", required = true) @RequestBody Carousel carousel) {
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
    @Operation(summary = "更新轮播图", description = "根据ID更新轮播图信息。更新成功后自动清除轮播图缓存。")
    @PostMapping("/update")
    public ResponseVO update(@Parameter(description = "轮播图实体（必须包含ID）", required = true) @RequestBody Carousel carousel) {
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
    @Operation(summary = "删除轮播图", description = "根据ID删除轮播图记录。删除成功后自动清除轮播图缓存。")
    @PostMapping("/delete/{id}")
    public ResponseVO delete(@Parameter(description = "轮播图ID", required = true, example = "1") @PathVariable Long id) {
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
     * 查询轮播图列表
     */
    @Operation(summary = "查询轮播图列表", description = "获取所有轮播图记录，按排序字段升序返回。")
    @GetMapping("/list")
    public ResponseVO list() {
        try {
            return getSuccessResponseVO(carouselService.listAllCarousels());
        } catch (Exception e) {
            log.error("查询轮播图列表失败: {}", e.getMessage(), e);
            return getFailResponseVO("查询轮播图列表失败: " + e.getMessage());
        }
    }

    /**
     * 手动清理轮播图缓存
     */
    @Operation(summary = "清理轮播图缓存", description = "手动清除轮播图相关所有缓存（含首页聚合缓存），适用于轮播图数据变更但缓存未自动刷新的场景。")
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
