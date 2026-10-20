package com.easymall.entity.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 购物车单项展示数据
 */
@Data
@Schema(description = "购物车单项")
public class CartItemVO implements Serializable {

    @Schema(description = "购物车项ID")
    private Long cartItemId;

    @Schema(description = "商品ID")
    private String productId;

    @Schema(description = "SKU ID")
    private Long skuId;

    @Schema(description = "SKU 哈希，下单时使用")
    private String propertyValueIdHash;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品封面")
    private String cover;

    @Schema(description = "规格信息")
    private String skuInfo;

    @Schema(description = "当前单价")
    private BigDecimal price;

    @Schema(description = "当前库存")
    private Integer stock;

    @Schema(description = "购买数量")
    private Integer quantity;

    @Schema(description = "小计")
    private BigDecimal subtotal;

    @Schema(description = "是否勾选：1-是，0-否")
    private Integer selected;

    @Schema(description = "是否失效")
    private Boolean invalid;

    @Schema(description = "失效原因")
    private String invalidReason;
}
