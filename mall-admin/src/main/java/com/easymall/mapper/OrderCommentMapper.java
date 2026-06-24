package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.Order.OrderComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单评论 Mapper
 */
@Mapper
public interface OrderCommentMapper extends BaseMapper<OrderComment> {
}
