package com.easymall.entity.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发起支付请求 DTO
 */
@Data
public class PayCreateDTO {

    /**
     * 订单 ID
     */
    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    /**
     * 支付方式：支付宝 / 微信支付 / 银联支付
     */
    @NotBlank(message = "支付方式不能为空")
    private String paymentMethod;
}
