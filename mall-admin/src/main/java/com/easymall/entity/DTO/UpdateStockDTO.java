package com.easymall.entity.DTO;

import lombok.Data;

import java.util.List;

@Data
public class UpdateStockDTO {

    /**
     * 商品ID
     */
    private String productId;

    /**
     * SKU库存列表
     */
    private List<SkuStock> skus;

    @Data
    public static class SkuStock {

        /**
         * SKU ID
         */
        private Long skuId;

        /**
         * SKU属性描述
         */
        private String propertyValueIds;

        /**
         * 库存数量
         */
        private Integer stock;
    }
}
