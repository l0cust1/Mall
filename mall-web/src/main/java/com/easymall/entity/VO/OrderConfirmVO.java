package com.easymall.entity.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "订单确认信息")
public class OrderConfirmVO {
    
    @Schema(description = "商品ID")
    private String productId;
    
    @Schema(description = "商品名称")
    private String productName;
    
    @Schema(description = "封面图URL")
    private String cover;
    
    @Schema(description = "规格属性值哈希")
    private String propertyValueIdHash;
    
    @Schema(description = "规格属性详情")
    private String propertyValueIds;
    
    @Schema(description = "价格")
    private BigDecimal price;
}