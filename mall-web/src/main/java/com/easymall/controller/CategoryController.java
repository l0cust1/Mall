package com.easymall.controller;

import com.easymall.entity.VO.CategoryVO;
import com.easymall.service.SysCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    }}