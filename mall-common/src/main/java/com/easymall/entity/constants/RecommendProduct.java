package com.easymall.entity.constants;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recommend_product")
@Schema(description = "推荐商品实体")
public class RecommendProduct {


    @Schema(description = "推荐商品ID", example = "1")
    private String id;

    /**
     * 关联 product_base.id
     */
    @Schema(description = "关联商品ID（product_base.id）", example = "100")
    private String productId;

    /**
     * 商品名称（冗余字段）
     */
    @Schema(description = "商品名称", example = "iPhone 15 Pro")
    private String productName;

    /**
     * 商品主图（冗余字段）
     */
    @Schema(description = "商品主图URL", example = "https://example.com/image.jpg")
    private String productImage;

    /**
     * 当前售价
     */
    @Schema(description = "当前售价", example = "5999.00")
    private BigDecimal price;

    /**
     * 原价（划线价）
     */
    @Schema(description = "原价（划线价）", example = "6999.00")
    private BigDecimal originalPrice;

    /**
     * 推荐类型：1-热门推荐 2-新品 3-精品
     */
    @Schema(description = "推荐类型：1-热门推荐 2-新品 3-精品", example = "1")
    private Integer recommendType;

    /**
     * 排序顺序
     */
    @Schema(description = "排序顺序（数值越小越靠前）", example = "10")
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    @Schema(description = "状态：0-禁用 1-启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "2026-01-01T10:00:00")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间", example = "2026-01-01T10:00:00")
    private LocalDateTime updateTime;
}