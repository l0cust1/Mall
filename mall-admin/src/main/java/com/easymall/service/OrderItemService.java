package com.easymall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.Order.OrderItem;

import java.util.List;

/**
 * 订单商品明细服务接口
 */
public interface OrderItemService extends IService<OrderItem> {
    
    /**
     * 根据订单号查询商品明细列表
     * @param orderId 订单号
     * @return 商品明细列表
     */
    List<OrderItem> getItemsByOrderId(String orderId);
}
