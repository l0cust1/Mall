package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.SysCategory;
import com.easymall.entity.SysProductProperty;
import com.easymall.entity.SysProductPropertyQuery;
import com.easymall.entity.vo.SysProductPropertyTreeVO;
import com.easymall.mapper.SysCategoryMapper;
import com.easymall.mapper.SysProductPropertyMapper;
import com.easymall.service.SysProductPropertyService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysProductPropertyServiceImpl extends ServiceImpl<SysProductPropertyMapper, SysProductProperty> implements SysProductPropertyService {

    @Resource
    private SysProductPropertyMapper sysProductPropertyMapper;

    @Resource
    private SysCategoryMapper sysCategoryMapper;


    @Override
    public IPage<SysProductPropertyTreeVO> findTreeListByParam(SysProductPropertyQuery query) {
        // 1. 先查询所有分类（支持分页参数）
        LambdaQueryWrapper<SysCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.orderByAsc(SysCategory::getSort);
        List<SysCategory> allCategories = sysCategoryMapper.selectList(categoryWrapper);

        if (allCategories.isEmpty()) {
            Page<SysProductPropertyTreeVO> page = new Page<>(query.getPageNum(), query.getPageSize(), 0);
            page.setRecords(new ArrayList<>());
            return page;
        }

        // 2. 提取所有分类ID
        List<String> categoryIds = allCategories.stream()
                .map(SysCategory::getCategoryId)
                .collect(Collectors.toList());

        // 3. 根据分类ID查询对应的属性
        LambdaQueryWrapper<SysProductProperty> propertyWrapper = new LambdaQueryWrapper<>();
        propertyWrapper.in(SysProductProperty::getCategoryId, categoryIds);
        propertyWrapper.orderByAsc(SysProductProperty::getPropertySort);
        List<SysProductProperty> allProperties = sysProductPropertyMapper.selectList(propertyWrapper);

        // 4. 构建分类ID到分类对象的映射
        var categoryMap = allCategories.stream()
                .collect(Collectors.toMap(SysCategory::getCategoryId, cat -> cat));

        // 5. 构建树形结构
        List<SysProductPropertyTreeVO> treeList = buildTree(allProperties, categoryMap);

        // 6. 手动分页
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int total = treeList.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);

        List<SysProductPropertyTreeVO> pageList = new ArrayList<>();
        if (fromIndex < total) {
            pageList = treeList.subList(fromIndex, toIndex);
        }

        Page<SysProductPropertyTreeVO> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(pageList);

        return page;
    }

    /**
     * 构建树形结构（三级：一级分类 -> 二级分类 -> 属性）
     * 注意：会显示所有分类，即使没有属性
     */
    private List<SysProductPropertyTreeVO> buildTree(List<SysProductProperty> properties,
                                                     java.util.Map<String, SysCategory> categoryMap) {
        List<SysProductPropertyTreeVO> result = new ArrayList<>();

        // 按父分类ID分组属性
        java.util.Map<String, List<SysProductProperty>> groupedByPCategory = properties.stream()
                .collect(Collectors.groupingBy(SysProductProperty::getPCategoryId));

        // 获取所有一级分类（pCategoryId为0或null的分类）
        List<SysCategory> firstLevelCategories = categoryMap.values().stream()
                .filter(cat -> cat.getPCategoryId() == null || "0".equals(cat.getPCategoryId()))
                .sorted((a, b) -> {
                    if (a.getSort() == null) return 1;
                    if (b.getSort() == null) return -1;
                    return a.getSort().compareTo(b.getSort());
                })
                .collect(Collectors.toList());

        // 遍历所有一级分类
        for (SysCategory firstCategory : firstLevelCategories) {
            String pCategoryId = firstCategory.getCategoryId();
            List<SysProductProperty> pCategoryProperties = groupedByPCategory.getOrDefault(pCategoryId, new ArrayList<>());

            SysProductPropertyTreeVO level1Node = new SysProductPropertyTreeVO();
            level1Node.setNodeId(pCategoryId);
            level1Node.setPCategoryId(pCategoryId);
            level1Node.setPropertyName(firstCategory.getCategoryName());
            level1Node.setLevel(1);
            level1Node.setNodeType(1);
            level1Node.setChildren(new ArrayList<>());

            // 获取该一级分类下的所有二级分类
            List<SysCategory> secondLevelCategories = categoryMap.values().stream()
                    .filter(cat -> pCategoryId.equals(cat.getPCategoryId()))
                    .sorted((a, b) -> {
                        if (a.getSort() == null) return 1;
                        if (b.getSort() == null) return -1;
                        return a.getSort().compareTo(b.getSort());
                    })
                    .collect(Collectors.toList());

            // 遍历所有二级分类
            for (SysCategory secondCategory : secondLevelCategories) {
                String categoryId = secondCategory.getCategoryId();
                List<SysProductProperty> categoryProperties = properties.stream()
                        .filter(p -> categoryId.equals(p.getCategoryId()))
                        .sorted((a, b) -> {
                            if (a.getPropertySort() == null) return 1;
                            if (b.getPropertySort() == null) return -1;
                            return a.getPropertySort().compareTo(b.getPropertySort());
                        })
                        .collect(Collectors.toList());

                SysProductPropertyTreeVO level2Node = new SysProductPropertyTreeVO();
                level2Node.setNodeId(categoryId);
                level2Node.setPCategoryId(pCategoryId);
                level2Node.setCategoryId(categoryId);
                level2Node.setPropertyName(secondCategory.getCategoryName());
                level2Node.setLevel(2);
                level2Node.setNodeType(2);
                level2Node.setChildren(new ArrayList<>());

                // 添加属性节点
                for (SysProductProperty property : categoryProperties) {
                    SysProductPropertyTreeVO propertyNode = convertToTreeVO(property);
                    propertyNode.setLevel(3);
                    propertyNode.setNodeType(3);
                    level2Node.getChildren().add(propertyNode);
                }

                level1Node.getChildren().add(level2Node);
            }

            result.add(level1Node);
        }

        return result;
    }

    private SysProductPropertyTreeVO convertToTreeVO(SysProductProperty property) {
        SysProductPropertyTreeVO vo = new SysProductPropertyTreeVO();
        BeanUtils.copyProperties(property, vo);
        vo.setLevel(0);
        return vo;
    }


    /**
     * 智能添加属性（自动填充p_category_id、propertyId、propertySort）
     */
    @Override
    @Transactional
    public void addSmartProperty(SysProductProperty property) {
        // 1. 查询分类信息
        SysCategory category = sysCategoryMapper.selectById(property.getCategoryId());
        if (category == null) {
            throw new RuntimeException("分类不存在：" + property.getCategoryId());
        }

        // 2. 自动设置父分类ID
        property.setPCategoryId(category.getPCategoryId());

        // 3. propertyId 和 propertySort 由 MyBatis-Plus 自动填充处理器生成



        // 4. 插入数据库
        sysProductPropertyMapper.insert(property);
    }


    /**
     * 根据propertyId更新属性
     */
    @Override
    @Transactional
    public void updateProperty(SysProductProperty property, String propertyId) {
        SysProductProperty existProperty = sysProductPropertyMapper.selectById(propertyId);
        if (existProperty == null) {
            throw new RuntimeException("属性不存在");
        }

        property.setPropertyId(propertyId);
        sysProductPropertyMapper.updateById(property);
    }

    /**
     * 根据propertyId删除属性
     */
    @Override
    @Transactional
    public void deleteProperty(String propertyId) {
        SysProductProperty property = sysProductPropertyMapper.selectById(propertyId);
        if (property == null) {
            throw new RuntimeException("属性不存在");
        }

        sysProductPropertyMapper.deleteById(propertyId);
    }


}