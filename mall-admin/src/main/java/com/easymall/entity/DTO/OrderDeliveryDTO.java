package com.easymall.entity.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 发货请求DTO
 */
@Data
public class OrderDeliveryDTO {

    /**
     * 订单ID
     */
    @NotBlank(message = "订单ID不能为空")
    private String orderId;

    /**
     * 收件人姓名
     */
    @NotBlank(message = "收件人不能为空")
    private String receiverName;

    /**
     * 收件人电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String receiverPhone;

    /**
     * 收件地址
     */
    @NotBlank(message = "收货地址不能为空")
    private String receiverAddress;

    /**
     * 发件人姓名
     */
    private String senderName;

    /**
     * 发件人电话
     */
    private String senderPhone;

    /**
     * 发货地址
     */
    private String senderAddress;

    /**
     * 物流公司
     */
    @NotBlank(message = "物流公司不能为空")
    private String expressCompany;

    /**
     * 物流单号
     */
    @NotBlank(message = "物流单号不能为空")
    private String expressNo;
}
