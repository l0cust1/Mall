package com.easymall.entity.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "商品详情响应体")
public class ProductDetailVO implements Serializable {

    @Schema(description = "商品ID")
    private String productId;

    @Schema(description = "商品名称")
    private String productName;

    @Schema(description = "商品描述")
    private String productDesc;

    @Schema(description = "商品封面图")
    private String cover;

    @Schema(description = "SKU列表")
    private List<ProductSkuVO> skus;

    @Data
    @Schema(description = "商品SKU信息")
    public static class ProductSkuVO implements Serializable {

        @Schema(description = "SKU ID")
        private Long id;

        @Schema(description = "规格属性值ID组合（逗号分隔）")
        private String propertyValueIds;

        @Schema(description = "规格属性值ID哈希")
        private String propertyValueIdHash;

        @Schema(description = "价格")
        private BigDecimal price;

        @Schema(description = "库存")
        private Integer stock;

        @Schema(description = "排序")
        private Integer sort;
    }
}
