package com.easymall.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("product_base")
public class ProductBase {
    @TableId("product_id")
    private String productId;
    private String productName;
    private String productDesc;
    private String cover;
    private LocalDateTime createTime;
    private String categoryId;
    private String pCategoryId;
    private Integer status; // 1-已上架，0-未上架
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Integer totalSale;
    private Integer commendType; // 1-已推荐，0-未推荐
    private String remark;
}
