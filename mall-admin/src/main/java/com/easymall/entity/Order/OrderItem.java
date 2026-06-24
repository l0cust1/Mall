package com.easymall.entity.Order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品明细表实体
 */
@Data
@TableName("order_item")
public class OrderItem {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderId;
    
    private String productId;
    
    private String productName;
    
    private String productCover;
    
    private String skuInfo;
    
    private BigDecimal price;
    
    private Integer quantity;
    
    private BigDecimal subtotal;
    
    private LocalDateTime createTime;
}
