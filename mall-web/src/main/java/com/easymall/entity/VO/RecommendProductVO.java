package com.easymall.entity.VO;



import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class RecommendProductVO implements Serializable {
    private String id;
    private String productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private Integer recommendType;  // 1-热门推荐 2-新品 3-精品
    private Integer sortOrder;
}