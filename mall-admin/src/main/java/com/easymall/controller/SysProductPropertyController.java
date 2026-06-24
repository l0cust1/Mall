package com.easymall.controller;

import com.easymall.entity.SysProductProperty;
import com.easymall.entity.SysProductPropertyQuery;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.SysProductPropertyService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sysProductProperty")
public class SysProductPropertyController extends ABaseController {

    @Resource
    private SysProductPropertyService sysProductPropertyService;



    /**
     * 根据条件分页查询（树形结构）
     */
    @RequestMapping("/loadPropertyTree")
    public ResponseVO loadPropertyTree(SysProductPropertyQuery query) {
        return getSuccessResponseVO(sysProductPropertyService.findTreeListByParam(query));
    }


    /**
     * 智能添加属性（自动填充p_category_id）
     * 前端只需传：categoryId、propertyName、coverType
     */
    @RequestMapping("/addSmart")
    public ResponseVO addSmart(@RequestBody SysProductProperty bean) {
        // 验证必填字段
        if (bean.getCategoryId() == null || bean.getCategoryId().isEmpty()) {
            throw new RuntimeException("分类ID不能为空");
        }
        if (bean.getPropertyName() == null || bean.getPropertyName().isEmpty()) {
            throw new RuntimeException("属性名称不能为空");
        }
        if (bean.getCoverType() == null) {
            throw new RuntimeException("封面类型不能为空");
        }
        
        sysProductPropertyService.addSmartProperty(bean);
        return getSuccessResponseVO(null);
    }




    /**
     * 根据propertyId修改对象
     */
    @RequestMapping("/updatePropertyById")
    public ResponseVO updatePropertyById(@RequestBody SysProductProperty bean, String propertyId) {
        sysProductPropertyService.updateProperty(bean, propertyId);
        return getSuccessResponseVO(null);
    }

    /**
     * 根据propertyId删除属性
     */
    @RequestMapping("/deletePropertyById")
    public ResponseVO deletePropertyById(String propertyId) {
        sysProductPropertyService.deleteProperty(propertyId);
        return getSuccessResponseVO(null);
    }

}
