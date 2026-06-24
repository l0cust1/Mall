package com.easymall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.SysCategory;
import com.easymall.entity.VO.CategoryVO;

import java.util.List;

public interface SysCategoryService extends IService<SysCategory> {
    /**
     * 首页获取一级分类（带缓存）
     */
    List<CategoryVO> getFirstLevelCategories();

}
