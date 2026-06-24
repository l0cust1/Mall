package com.easymall.entity.VO;



import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class SeckillProductVO implements Serializable {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal originalPrice;
    private BigDecimal seckillPrice;
    private Integer remainingStock;
    private Integer totalStock;
}