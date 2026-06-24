package com.easymall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("product_property")
public class ProductProperty {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String productId;
    private String propertyId;
    private String propertyName;
    private Integer propertySort;
    private String propertyValueId;
    private String propertyValue;
    private String propertyRemark;
    private Integer sort;
}