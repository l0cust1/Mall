package com.easymall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 购物车项
 */
@Data
@TableName("cart_item")
public class CartItem {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private String productId;

    /**
     * SKU ID
     */
    private Long skuId;

    /**
     * SKU 哈希，下单时传给 order/create
     */
    private String propertyValueIdHash;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 是否勾选：1-是，0-否
     */
    private Integer selected;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
