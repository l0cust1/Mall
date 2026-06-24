package com.easymall.entity.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

// 发布商品的请求DTO（适配前端传参）
@Data
public class ProductPublishDTO {
    private String productId; // 编辑时传入，新增时为空
    private String productName;
    private String productDesc;
    private String cover;
    private String categoryId;
    private String pCategoryId;
    private Integer status;
    private Integer commendType;
    private String remark; // 备注
    // 商品属性（尺码/颜色/段位）
    private List<ProductPropertyDTO> properties;
    // SKU信息
    private List<ProductSkuDTO> skus;
    @Data
    public static class ProductSkuDTO {
        private String propertyValueIds; // 如"PROP_VAL_123,PROP_VAL_456"
        private BigDecimal price;
        private Integer stock;
        private Integer sort;
    }
    @Data
    public static class ProductPropertyDTO {
        private String propertyName;
        private Integer propertySort;
        private List<ProductPropertyValueDTO> propertyValues;

        @Data
        public static class ProductPropertyValueDTO {
            private String propertyValue;
            private String propertyRemark;
            private Integer sort;
        }
    }
}