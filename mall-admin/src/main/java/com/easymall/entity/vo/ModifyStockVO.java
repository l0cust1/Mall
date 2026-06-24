package com.easymall.entity.vo;

import lombok.Data;

@Data
public class ModifyStockVO {

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * SKU属性描述（如"红色/XL"）
     */
    private String propertyValueIds;
}
