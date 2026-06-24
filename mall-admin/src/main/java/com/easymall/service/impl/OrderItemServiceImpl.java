package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.Order.OrderItem;
import com.easymall.mapper.OrderItemMapper;
import com.easymall.service.OrderItemService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 订单商品明细服务实现类
 */
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemMapper, OrderItem> implements OrderItemService {

    @Resource
    private OrderItemMapper orderItemMapper;
    /**
     * 根据订单号查询商品明细列表
     */
    @Override
    public List<OrderItem> getItemsByOrderId(String orderId) {
        return list(new LambdaQueryWrapper<OrderItem>()
            .eq(OrderItem::getOrderId, orderId));
    }
}
