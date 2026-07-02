package com.easymall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.SysCategory;
import com.easymall.entity.VO.CategoryVO;
import com.easymall.entity.VO.SysCategoryTreeVO;

import java.util.List;

public interface SysCategoryService extends IService<SysCategory> {
    /**
     * 首页获取一级分类（带缓存）
     */
    List<CategoryVO> getFirstLevelCategories();

    /**
     * 根据一级分类ID查询该分类下所有二级分类 + 每个二级分类绑定的商品列表
     * @param pCategoryId 一级分类ID
     * @return 二级分类树形列表（含商品数组）
     */
    List<SysCategoryTreeVO> getSubCategoryWithProduct(String pCategoryId);
}
