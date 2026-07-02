package com.easymall.controller;

import com.easymall.entity.VO.CategoryVO;
import com.easymall.entity.VO.SysCategoryTreeVO;
import com.easymall.result.ApiResult;
import com.easymall.service.SysCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类控制器（首页分类导航）
 */
@RestController
@RequestMapping("/categoryV1")
@Slf4j
public class CategoryController {

    @Autowired
    private SysCategoryService categoryService;

    /**
     * 获取首页一级分类导航
     * 前端路径：GET /api/v1/category/first-level
     */
    @GetMapping("/first-level")
    public List<CategoryVO> getFirstLevelCategories() {
        log.info("收到获取一级分类请求");
        return categoryService.getFirstLevelCategories();
    }

    /**
     * 根据一级分类ID查询该分类下所有二级分类 + 每个二级分类绑定的商品列表
     * 请求方式：GET
     * 接口地址：/category/getSubCategoryWithProduct
     * 请求入参：pCategoryId（一级分类 ID）
     * 返回结构：ResponseVO<List<SysCategoryTreeVO>>
     */
    @GetMapping("/getSubCategoryWithProduct")
    public ApiResult<List<SysCategoryTreeVO>> getSubCategoryWithProduct(@RequestParam("pCategoryId") String pCategoryId) {
        log.info("收到获取二级分类及商品列表请求，一级分类ID：{}", pCategoryId);
        List<SysCategoryTreeVO> result = categoryService.getSubCategoryWithProduct(pCategoryId);
        return ApiResult.success(result);
    }
}