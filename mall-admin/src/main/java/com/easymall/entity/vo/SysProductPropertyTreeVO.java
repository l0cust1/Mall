package com.easymall.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 商品属性树形结构VO
 * 支持三级结构：一级分类 -> 二级分类 -> 属性列表
 */
@Data
public class SysProductPropertyTreeVO {
    
    /**
     * 节点ID（一级分类用pCategoryId，二级分类用categoryId，属性用propertyId）
     */
    private String nodeId;
    
    /**
     * 属性ID
     */
    private String propertyId;
    
    /**
     * 属性名称
     */
    private String propertyName;
    
    /**
     * 父分类ID（一级分类ID）
     */
    private String pCategoryId;
    
    /**
     * 分类ID（二级分类ID）
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
    
    /**
     * 层级（1:一级分类，2:二级分类，3:属性）
     */
    private Integer level;
    
    /**
     * 节点类型（1:一级分类，2:二级分类，3:属性）
     */
    private Integer nodeType;
    
    /**
     * 子节点列表（二级分类或属性）
     */
    private List<SysProductPropertyTreeVO> children;
}
