package com.easymall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.SysCategory;
import com.easymall.entity.SysCategoryQuery;
import com.easymall.entity.vo.SysCategoryTreeVO;

import java.util.List;

public interface SysCategoryService extends IService<SysCategory> {
    IPage<SysCategory> findListByParam(SysCategoryQuery query);
    
    /**
     * 树形结构分页查询
     */
    IPage<SysCategoryTreeVO> findTreeListByParam(SysCategoryQuery query);
    
    /**
     * 查询所有分类列表（用于下拉选择）
     */
    List<SysCategory> listAllCategories();
    
    void add(SysCategory bean);

    // 根据 categoryId 查询单条数据
    SysCategory getSysCategoryByCategoryId(String categoryId);
    void updateSysCategoryByCategoryId(SysCategory bean, String categoryId);
    
    /**
     * 删除分类（级联删除子分类）
     */
    void deleteSysCategoryByCategoryId(String categoryId);
    
    /**
     * 批量删除分类
     */
    void deleteBatch(List<String> categoryIds);
}
