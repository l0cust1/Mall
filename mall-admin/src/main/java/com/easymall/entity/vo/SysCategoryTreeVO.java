package com.easymall.entity.vo;

import com.easymall.entity.SysProductProperty;
import lombok.Data;
import java.util.List;

/**
 * 分类树形结构VO
 */
@Data
public class SysCategoryTreeVO {
    /**
     * 分类ID
     */
    private String categoryId;

    /**
     * 分类名称
     */
    private String categoryName;

    /**
     * 父分类ID
     */
    private String pCategoryId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子分类列表
     */
    private List<SysCategoryTreeVO> children;

    /**
     * 层级深度（0为根节点）
     */
    private Integer level;
    
    /**
     * 该分类下的商品属性列表
     */
    private List<SysProductProperty> propertyList;
}
