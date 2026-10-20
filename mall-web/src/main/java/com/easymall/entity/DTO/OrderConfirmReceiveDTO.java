package com.easymall.entity.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 确认收货请求 DTO
 */
@Data
public class OrderConfirmReceiveDTO {

    /**
     * 订单 ID
     */
    @NotBlank(message = "订单ID不能为空")
    private String orderId;
}
