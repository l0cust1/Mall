package com.easymall.controller;

import com.easymall.entity.constants.RecommendProduct;
import com.easymall.service.RecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台推荐商品管理控制器
 */
@RestController
@RequestMapping("/api/admin/recommend")

@Slf4j
public class RecommendAdminController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 新增推荐商品
     * POST /admin/api/v1/recommend
     */
    @PostMapping

    public String saveRecommend(@RequestBody RecommendProduct entity) {
        log.info("新增推荐商品，productId：{}", entity.getProductId());
        recommendService.saveRecommend(entity);
        return "新增成功";
    }

    /**
     * 修改推荐商品
     * PUT /admin/api/v1/recommend
     */
    @PutMapping

    public String updateRecommend(@RequestBody RecommendProduct entity) {
        log.info("修改推荐商品，ID：{}", entity.getId());
        recommendService.updateRecommend(entity);
        return "修改成功";
    }

    /**
     * 删除推荐商品
     * DELETE /admin/api/v1/recommend/{id}
     */
    @DeleteMapping("/{id}")

    public String deleteRecommend(@PathVariable Long id) {
        log.info("删除推荐商品，ID：{}", id);
        recommendService.deleteRecommend(id);
        return "删除成功";
    }

    /**
     * 手动清除缓存（运营后台按钮触发）
     * POST /admin/api/v1/recommend/clear-cache
     */
    @PostMapping("/clear-cache")

    public String clearCache() {
        log.info("手动清除推荐商品缓存");
        recommendService.clearRecommendCache();
        return "缓存清除成功";
    }
}