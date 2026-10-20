package com.easymall.entity.Order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单支付表实体
 */
@Data
@TableName("order_payment")
public class OrderPayment {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderId;

    private String paymentNo;

    private String paymentMethod;

    private LocalDateTime paymentTime;

    private BigDecimal paymentAmount;

    /**
     * 支付状态：0-未支付，1-已支付，2-支付失败
     */
    private Integer paymentStatus;

    private LocalDateTime createTime;
}
