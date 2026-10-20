package com.easymall.entity.DTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.util.List;

/**
 * 创建订单请求 DTO
 */
@Data
public class OrderCreateDTO {

    /**
     * 收货地址 ID
     */
    @NotNull(message = "收货地址不能为空")
    private Long addressId;

    /**
     * 收货人信息（姓名 手机号 详细地址）
     */
    @NotBlank(message = "收货人信息不能为空")
    private String buyerInfo;

    /**
     * 买家备注
     */
    private String buyerRemark;

    /**
     * 订单商品明细列表
     */
    @NotEmpty(message = "订单商品不能为空")
    @Valid
    private List<OrderItemDTO> orderItemList;

    /**
     * 购物车结算时传入的购物车项ID，下单成功后自动清理
     */
    private List<Long> cartItemIds;

    @Data
    public static class OrderItemDTO {

        /**
         * 商品 ID
         */
        @NotBlank(message = "商品ID不能为空")
        private String productId;

        /**
         * SKU 唯一标识
         */
        @NotBlank(message = "SKU不能为空")
        private String propertyValueIdHash;

        /**
         * 购买数量
         */
        @NotNull(message = "购买数量不能为空")
        @Positive(message = "购买数量必须大于0")
        private Integer quantity;
    }
}
