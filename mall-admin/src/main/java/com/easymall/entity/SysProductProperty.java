package com.easymall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 商品属性（SKU属性）
 */
@Data
@TableName("sys_product_property")
public class SysProductProperty {
    
    /**
     * 属性ID（自动生成）
     */
    @TableId(value = "property_id", type = IdType.ASSIGN_ID)
    private String propertyId;
    
    /**
     * 属性名称（如：颜色、尺码、型号等）
     */
    private String propertyName;
    
    /**
     * 父分类ID（可选，用于层级属性）
     */
    private String pCategoryId;
    
    /**
     * 分类ID（关联到sys_category表）
     */
    private String categoryId;
    
    /**
     * 属性排序
     */
    private Integer propertySort;
    
    /**
     * 封面类型（0:不包含图片，1:包含图片）
     */
    private Integer coverType;
}
