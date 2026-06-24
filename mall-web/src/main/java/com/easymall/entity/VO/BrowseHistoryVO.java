package com.easymall.entity.VO;



import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class BrowseHistoryVO implements Serializable {
    private Long productId;
    private String productName;
    private String productImage;
    private BigDecimal price;
    private Long browseTime;  // 浏览时间戳
}