package com.easymall.entity.VO;

import com.easymall.entity.constants.ProductBase;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分类树形VO（用于返回二级分类及其绑定的商品列表）
 */
@Data
public class SysCategoryTreeVO implements Serializable {
    private String categoryId;
    private String categoryName;
    private String pCategoryId;
    private Integer sort;
    /**
     * 该二级分类下已上架的商品列表
     */
    private List<ProductBase> products;
}
