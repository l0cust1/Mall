package com.easymall.controller;

import com.easymall.entity.constants.RecommendProduct;
import com.easymall.service.RecommendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 后台推荐商品管理控制器
 */
@RestController
@RequestMapping("/admin/recommend")
@Tag(name = "推荐商品管理", description = "后台推荐商品的增删改查及缓存管理接口")
@Slf4j
public class RecommendAdminController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 新增推荐商品
     * POST /admin/api/v1/recommend
     */
    @PostMapping
    @Operation(summary = "新增推荐商品", description = "创建一个新的推荐商品记录")
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
    @Operation(summary = "修改推荐商品", description = "更新已有推荐商品的信息")
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
    @Operation(summary = "删除推荐商品", description = "根据ID删除推荐商品")
    public String deleteRecommend(@PathVariable @Parameter(description = "推荐商品ID", required = true) Long id) {
        log.info("删除推荐商品，ID：{}", id);
        recommendService.deleteRecommend(id);
        return "删除成功";
    }

    /**
     * 查询推荐商品列表
     * GET /admin/recommend/list
     */
    @GetMapping("/list")
    @Operation(summary = "查询推荐商品列表", description = "获取所有推荐商品记录，按排序字段升序返回")
    public java.util.List<RecommendProduct> list() {
        return recommendService.lambdaQuery()
                .orderByAsc(RecommendProduct::getSortOrder)
                .list();
    }

    /**
     * 手动清除缓存（运营后台按钮触发）
     * POST /admin/api/v1/recommend/clear-cache
     */
    @PostMapping("/clear-cache")
    @Operation(summary = "清除推荐缓存", description = "手动清除推荐商品的缓存数据")
    public String clearCache() {
        log.info("手动清除推荐商品缓存");
        recommendService.clearRecommendCache();
        return "缓存清除成功";
    }
}