package com.easymall.entity.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 加入购物车请求
 * skuId 与 propertyValueIdHash 至少传一个；优先使用 skuId
 */
@Data
public class CartAddDTO {

    @NotBlank(message = "商品ID不能为空")
    private String productId;

    private Long skuId;

    private String propertyValueIdHash;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    private Integer quantity;
}
