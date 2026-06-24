package com.easymall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.Order.OrderPayment;

/**
 * 订单支付服务接口
 */
public interface OrderPaymentService extends IService<OrderPayment> {
    
    /**
     * 根据订单号查询支付信息
     * @param orderId 订单号
     * @return 支付信息
     */
    OrderPayment getPaymentByOrderId(String orderId);
}
