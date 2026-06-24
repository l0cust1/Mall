package com.easymall.entity;

import lombok.Data;

/**
 * 订单评论查询条件
 */
@Data
public class OrderCommentQuery {

    /**
     * 用户昵称（模糊查询）
     */
    private String userName;

    /**
     * 商品名称（模糊查询）
     */
    private String productName;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 15;
}
