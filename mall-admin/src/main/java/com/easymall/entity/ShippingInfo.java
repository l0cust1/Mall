package com.easymall.entity;

import lombok.Data;

@Data
public class ShippingInfo {
    /**
     * 发货人姓名
     */
    private String senderName;

    /**
     * 发货人电话
     */
    private String senderPhone;

    /**
     * 发货地址
     */
    private String senderAddress;

    /**
     * 默认快递公司
     */
    private String defaultExpress;

    /**
     * 备注信息
     */
    private String remark;
}
