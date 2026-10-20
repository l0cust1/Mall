package com.easymall.entity.VO;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 创建订单响应 VO
 */
@Data
public class OrderCreateVO {

    /**
     * 订单 ID
     */
    private String orderId;

    /**
     * 实际支付金额
     */
    private BigDecimal payAmount;

    /**
     * 订单状态
     */
    private Integer orderStatus;
}
