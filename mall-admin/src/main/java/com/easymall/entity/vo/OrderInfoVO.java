package com.easymall.entity.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单信息VO - 用于订单列表展示
 */
@Data
public class OrderInfoVO {
    
    /**
     * 订单时间
     */
    private LocalDateTime orderTime;
    
    /**
     * 订单号
     */
    private String orderId;
    
    /**
     * 买家信息（姓名、电话、地址）
     */
    private String buyerInfo;
    
    /**
     * 买家备注
     */
    private String buyerRemark;
    
    /**
     * 订单商品明细列表
     */
    private List<OrderItemVO> items;
    
    /**
     * 订单总数量
     */
    private Integer totalCount;
    
    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;
    
    /**
     * 实付金额
     */
    private BigDecimal payAmount;
    
    /**
     * 订单状态：0-已删除，1-待付款，2-已付款待发货，3-已发货，4-已完成，5-交易取消，6-交易关闭，7-已退款交易关闭
     */
    private Integer orderStatus;
    
    /**
     * 订单状态文本
     */
    private String orderStatusText;
    
    /**
     * 订单商品明细VO
     */
    @Data
    public static class OrderItemVO {
        /**
         * 商品图片
         */
        private String productCover;
        
        /**
         * 商品名称
         */
        private String productName;
        
        /**
         * SKU信息（颜色分类等）
         */
        private String skuInfo;
        
        /**
         * 价格
         */
        private BigDecimal price;
        
        /**
         * 数量
         */
        private Integer quantity;
        
        /**
         * 小计
         */
        private BigDecimal subtotal;
    }
}
