package com.easymall.entity.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 查询支付状态响应 VO
 */
@Data
public class PayQueryVO {

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 支付方式（支付宝/微信等）
     */
    private String paymentMethod;

    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 支付状态：0-待支付，1-支付成功，2-支付失败，3-已退款
     */
    private Integer paymentStatus;

    /**
     * 订单状态：0-已删除，1-待付款，2-已付款待发货，3-已发货，4-已完成，5-交易取消，6-交易关闭，7-已退款交易关闭
     */
    private Integer orderStatus;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;
}
