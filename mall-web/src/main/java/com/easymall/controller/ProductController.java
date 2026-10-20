package com.easymall.controller;

import com.easymall.entity.VO.ProductDetailVO;
import com.easymall.result.ApiResult;
import com.easymall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品控制器（前端用户侧）
 */
@Tag(name = "商品模块", description = "商品详情等前端用户接口")
@RestController
@RequestMapping("/product")
@Slf4j
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品详情
     * GET /api/v1/product/detail/{productId}
     */
    @Operation(summary = "获取商品详情", description = "返回商品基础信息（ID、名称、描述、封面图）及该商品的所有SKU列表（规格、价格、库存）。")
    @GetMapping("/detail/{productId}")
    public ApiResult<ProductDetailVO> getProductDetail(@PathVariable String productId) {
        log.info("获取商品详情，productId: {}", productId);

        ProductDetailVO vo = productService.getProductDetail(productId);
        if (vo == null) {
            return ApiResult.error(404, "商品不存在");
        }

        return ApiResult.success(vo);
    }
}
