package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.Order.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单主表 Mapper
 */
@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
