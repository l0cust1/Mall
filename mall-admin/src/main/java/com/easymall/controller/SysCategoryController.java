package com.easymall.controller;

import com.easymall.entity.SysCategory;
import com.easymall.entity.SysCategoryQuery;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.SysCategoryService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sysCategory")
public class SysCategoryController extends ABaseController {

    @Resource
    private SysCategoryService sysCategoryService;

    /**
     * 根据条件分页查询（普通列表）
     */
//    @RequestMapping("/loadCategory")
//    public ResponseVO loadCategory(SysCategoryQuery query) {
//        return getSuccessResponseVO(sysCategoryService.findListByParam(query));
//    }

    /**
     * 根据条件分页查询（树形结构）
     */
    @RequestMapping("/loadCategoryTree")
    public ResponseVO loadCategoryTree(SysCategoryQuery query) {
        return getSuccessResponseVO(sysCategoryService.findTreeListByParam(query));
    }

    /**
     * 查询所有分类列表（用于下拉选择）
     */
//    @RequestMapping("/listAllCategories")
//    public ResponseVO listAllCategories() {
//        return getSuccessResponseVO(sysCategoryService.listAllCategories());
//    }

    /**
     * 新增
     */
    @RequestMapping("/add")
    public ResponseVO add( @RequestBody SysCategory bean) {
        sysCategoryService.add(bean);
        return getSuccessResponseVO(null);
    }

    /**
     * 批量新增
     */
//    @RequestMapping("/addBatch")
//    public ResponseVO addBatch(@RequestBody List<SysCategory> listBean) {
//        sysCategoryService.addBatch(listBean);
//        return getSuccessResponseVO(null);
//    }
//
//    /**
//     * 批量新增/修改
//     */
//    @RequestMapping("/addOrUpdateBatch")
//    public ResponseVO addOrUpdateBatch(@RequestBody List<SysCategory> listBean) {
//        sysCategoryService.addBatch(listBean);
//        return getSuccessResponseVO(null);
//    }

    /**
     * 根据CategoryId查询对象
     */
    @RequestMapping("/getSysCategoryByCategoryId")
    public ResponseVO getSysCategoryByCategoryId(String categoryId) {
        return getSuccessResponseVO(sysCategoryService.getSysCategoryByCategoryId(categoryId));
    }

    /**
     * 根据CategoryId修改对象
     */
    @RequestMapping("/updateSysCategoryByCategoryId")
    public ResponseVO updateSysCategoryByCategoryId(@RequestBody SysCategory bean, String categoryId) {
        sysCategoryService.updateSysCategoryByCategoryId(bean, categoryId);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据CategoryId删除（级联删除子分类）
     */
    @RequestMapping("/deleteSysCategoryByCategoryId")
    public ResponseVO deleteSysCategoryByCategoryId(String categoryId) {
        sysCategoryService.deleteSysCategoryByCategoryId(categoryId);
        return getSuccessResponseVO(null);
    }

    /**
     * 批量删除分类
     */
//    @RequestMapping("/deleteBatch")
//    public ResponseVO deleteBatch(@RequestBody List<String> categoryIds) {
//        sysCategoryService.deleteBatch(categoryIds);
//        return getSuccessResponseVO(null);
//    }
    }