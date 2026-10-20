package com.easymall.entity.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 软删除订单请求 DTO
 */
@Data
public class OrderSoftDeleteDTO {

    /**
     * 订单 ID
     */
    @NotBlank(message = "订单ID不能为空")
    private String orderId;
}
