package com.easymall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.SysProductProperty;
import com.easymall.entity.SysProductPropertyQuery;
import com.easymall.entity.vo.SysProductPropertyTreeVO;

import java.util.List;

public interface SysProductPropertyService extends IService<SysProductProperty> {

    IPage<SysProductPropertyTreeVO> findTreeListByParam(SysProductPropertyQuery query);

    void addSmartProperty(SysProductProperty property);

    void updateProperty(SysProductProperty property, String propertyId);

    void deleteProperty(String propertyId);

}
