package com.easymall.entity.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单商品明细表实体
 */
@Data
public class OrderItemVO {

    private Long id;

    private String orderId;

    private String productId;

    private String productName;

    private String productCover;

    private String skuInfo;

    private BigDecimal price;

    private Integer quantity;

    private BigDecimal subtotal;

    /**
     * 订单状态（关联 order_info 表）
     * 1-待付款, 2-待发货, 3-待收货, 4-已完成, 5-已取消, 6-已关闭, 7-已退款
     */
    private Integer orderStatus;

    private LocalDateTime createTime;

}
