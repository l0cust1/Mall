package com.easymall.entity;

import lombok.Data;

/**
 * 分类列表查询条件
 */
@Data
public class SysCategoryQuery {

    /**
     * 分类名称（模糊查询）
     */
    private String categoryName;

    /**
     * 父分类ID
     */
    private String pCategoryId;

    // ==================== 分页参数 ====================
    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

}