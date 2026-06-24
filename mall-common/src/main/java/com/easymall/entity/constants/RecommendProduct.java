package com.easymall.entity.constants;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("recommend_product")
public class RecommendProduct {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 关联 product_base.id
     */
    private Long productId;

    /**
     * 商品名称（冗余字段）
     */
    private String productName;

    /**
     * 商品主图（冗余字段）
     */
    private String productImage;

    /**
     * 当前售价
     */
    private BigDecimal price;

    /**
     * 原价（划线价）
     */
    private BigDecimal originalPrice;

    /**
     * 推荐类型：1-热门推荐 2-新品 3-精品
     */
    private Integer recommendType;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 状态：0-禁用 1-启用
     */
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}