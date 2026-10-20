package com.easymall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("order_logistics")
public class OrderLogistics {

    /**
     * 主键ID，自增
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    private String orderId;

    /**
     * 快递公司
     */
    private String expressCompany;

    /**
     * 快递单号
     */
    private String expressNo;

    /**
     * 发货时间
     */
    private LocalDateTime deliveryTime;

    /**
     * 收件人姓名
     */
    private String receiverName;

    /**
     * 收件人手机号
     */
    private String receiverPhone;

    /**
     * 收件地址
     */
    private String receiverAddress;

    /**
     * 创建时间，数据库默认CURRENT_TIMESTAMP
     */
    private LocalDateTime createTime;

    /**
     * 更新时间，自动更新CURRENT_TIMESTAMP
     */
    private LocalDateTime updateTime;
}