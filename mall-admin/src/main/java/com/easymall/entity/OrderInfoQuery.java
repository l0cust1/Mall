package com.easymall.entity;

import lombok.Data;

/**
 * 订单列表查询条件
 */
@Data
public class OrderInfoQuery {

    /**
     * 订单ID（精确查询）
     */
    private String orderId;

    /**
     * 商品名称（模糊查询）
     */
    private String productName;

    /**
     * 订单状态：0-已删除，1-待付款，2-已付款待发货，3-已发货，4-已完成，5-交易取消，6-交易关闭，7-已退款交易关闭
     */
    private Integer orderStatus;

    // ==================== 分页参数 ====================
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 15;
}
