package com.easymall.entity.Order;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单评论实体
 */
@Data
@TableName("order_comment")
public class OrderComment {
    
    /**
     * 评论ID
     */
    @TableId("comment_id")
    private String commentId;
    
    /**
     * 订单ID
     */
    private String orderId;
    
    /**
     * 用户ID
     */
    private String userId;
    
    /**
     * 用户昵称
     */
    private String userName;
    
    /**
     * 用户头像
     */
    private String userAvatar;
    
    /**
     * 商品ID
     */
    private String productId;
    
    /**
     * 商品名称
     */
    private String productName;
    
    /**
     * 商品图片
     */
    private String productCover;
    
    /**
     * SKU信息
     */
    private String skuInfo;
    
    /**
     * 评论类型：1-初次评价，2-追加评价
     */
    private Integer commentType;
    
    /**
     * 评论内容
     */
    private String commentContent;
    
    /**
     * 评分：1-5星
     */
    private Integer commentScore;
    
    /**
     * 评论时间
     */
    private LocalDateTime commentTime;
    
    /**
     * 商家回复内容
     */
    private String replyContent;
    
    /**
     * 商家回复时间
     */
    private LocalDateTime replyTime;
    
    /**
     * 是否删除：0-正常，1-已删除
     */
    private Integer isDeleted;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
