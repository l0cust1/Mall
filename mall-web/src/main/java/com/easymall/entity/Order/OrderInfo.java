package com.easymall.entity.Order;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单主表实体
 */
@Data
@TableName("order_info")
public class OrderInfo {

    @TableId("order_id")
    private String orderId;

    private LocalDateTime orderTime;

    private String userId;

    private String buyerInfo;

    private BigDecimal totalAmount;

    private BigDecimal payAmount;

    /**
     * 订单状态：0-已删除，1-待付款，2-已付款待发货，3-已发货，4-已完成，5-交易取消，6-交易关闭，7-已退款交易关闭
     */
    private Integer orderStatus;

    private String buyerRemark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer isDeleted;
}
