package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CartItemMapper extends BaseMapper<CartItem> {

    /**
     * 统计用户购物车商品总数量（按数量累加）
     */
    @Select("SELECT COALESCE(SUM(quantity), 0) FROM cart_item WHERE user_id = #{userId}")
    Integer sumQuantityByUserId(@Param("userId") Long userId);
}
