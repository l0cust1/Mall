package com.easymall.service;

import com.easymall.entity.ShippingInfo;

public interface ShippingInfoService {
    /**
     * 获取发货信息
     */
    ShippingInfo getShippingInfo();

    /**
     * 保存发货信息
     */
    void saveShippingInfo(ShippingInfo shippingInfo);
}
