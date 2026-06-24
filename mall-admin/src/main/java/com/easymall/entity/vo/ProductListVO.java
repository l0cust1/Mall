package com.easymall.entity.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductListVO {
    private String productId;
    private String categoryId;
    private String productName;
    private String productCategoryName;
    private String cover;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer totalStock; // 总库存
    private Integer skuCount;   // SKU数量
    private Integer status;

    private Integer commendType;
}