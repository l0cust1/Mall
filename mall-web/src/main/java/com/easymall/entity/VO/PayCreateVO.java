package com.easymall.entity.VO;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 发起支付响应 VO
 */
@Data
public class PayCreateVO {

    /**
     * 支付流水号
     */
    private String paymentNo;

    /**
     * 支付跳转链接 / 二维码地址
     */
    private String payUrl;

    /**
     * 支付金额
     */
    private BigDecimal paymentAmount;
}
