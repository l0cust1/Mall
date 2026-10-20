package com.easymall.service;

import com.easymall.entity.VO.OrderConfirmVO;
import com.easymall.entity.VO.ProductDetailVO;

public interface ProductService {

    /**
     * 获取商品详情（基础信息 + SKU列表）
     */
    ProductDetailVO getProductDetail(String productId);

    /**
     * 获取订单确认信息
     * @param property_value_id_hash SKU属性值ID哈希
     * @return 订单确认信息
     */
    OrderConfirmVO getOrderConfirmInfo(String property_value_id_hash);
}
