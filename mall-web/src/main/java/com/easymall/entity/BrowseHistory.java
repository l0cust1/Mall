package com.easymall.entity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("browse_history")
public class BrowseHistory {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 关联 product_base.id
     */
    private Long productId;

    /**
     * 商品名称（冗余快照）
     */
    private String productName;

    /**
     * 商品主图（冗余快照）
     */
    private String productImage;

    /**
     * 浏览时的价格（冗余快照，用于展示历史价格）
     */
    private BigDecimal price;

    /**
     * 浏览时间
     */
    private LocalDateTime browseTime;

    private LocalDateTime createTime;
}