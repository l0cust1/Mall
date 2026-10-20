package com.easymall.entity.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 修改购物车商品数量
 */
@Data
public class CartQuantityDTO {

    @NotNull(message = "购物车项ID不能为空")
    private Long cartItemId;

    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    private Integer quantity;
}
