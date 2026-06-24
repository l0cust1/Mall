package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.Order.OrderLogistics;
import com.easymall.mapper.OrderLogisticsMapper;
import com.easymall.service.OrderLogisticsService;
import org.springframework.stereotype.Service;

/**
 * 订单物流服务实现类
 */
@Service
public class OrderLogisticsServiceImpl extends ServiceImpl<OrderLogisticsMapper, OrderLogistics> implements OrderLogisticsService {

    /**
     * 根据订单号查询物流信息
     */
    @Override
    public OrderLogistics getLogisticsByOrderId(String orderId) {
        return getOne(new LambdaQueryWrapper<OrderLogistics>()
            .eq(OrderLogistics::getOrderId, orderId));
    }
}
