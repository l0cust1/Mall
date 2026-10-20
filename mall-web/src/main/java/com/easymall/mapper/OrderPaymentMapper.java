package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.Order.OrderPayment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单支付表 Mapper
 */
@Mapper
public interface  OrderPaymentMapper extends BaseMapper<OrderPayment> {
}
