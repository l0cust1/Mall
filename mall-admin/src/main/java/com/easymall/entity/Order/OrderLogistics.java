package com.easymall.entity.Order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单物流表实体
 */
@Data
@TableName("order_logistics")
public class OrderLogistics {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String orderId;
    
    private String expressCompany;
    
    private String expressNo;
    
    private LocalDateTime deliveryTime;
    
    private String receiverName;
    
    private String receiverPhone;
    
    private String receiverAddress;
    
    private LocalDateTime createTime;
    
    private LocalDateTime updateTime;
}
