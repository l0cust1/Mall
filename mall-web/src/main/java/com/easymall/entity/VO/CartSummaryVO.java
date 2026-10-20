package com.easymall.entity.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 购物车列表与汇总数据
 */
@Data
@Schema(description = "购物车汇总")
public class CartSummaryVO implements Serializable {

    @Schema(description = "购物车项列表")
    private List<CartItemVO> items;

    @Schema(description = "所有商品数量合计")
    private Integer totalQuantity;

    @Schema(description = "已勾选且有效商品数量合计")
    private Integer selectedQuantity;

    @Schema(description = "已勾选商品项数")
    private Integer selectedItemCount;

    @Schema(description = "已勾选且有效商品金额")
    private BigDecimal selectedAmount;

    @Schema(description = "有效商品是否已全选")
    private Boolean allSelected;
}
