package com.easymall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.Order.OrderLogistics;

/**
 * 订单物流服务接口
 */
public interface OrderLogisticsService extends IService<OrderLogistics> {
    
    /**
     * 根据订单号查询物流信息
     * @param orderId 订单号
     * @return 物流信息
     */
     OrderLogistics getLogisticsByOrderId(String orderId);
}
