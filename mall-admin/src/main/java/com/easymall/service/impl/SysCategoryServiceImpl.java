package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.SysCategory;
import com.easymall.entity.SysCategoryQuery;
import com.easymall.entity.vo.SysCategoryTreeVO;
import com.easymall.mapper.SysCategoryMapper;
import com.easymall.service.SysCategoryService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class SysCategoryServiceImpl extends ServiceImpl<SysCategoryMapper, SysCategory> implements SysCategoryService {

    @Resource
    private SysCategoryMapper sysCategoryMapper;
    

    
    /**
     * 条件 + 分页查询
     */
    @Override
    public IPage<SysCategory> findListByParam(SysCategoryQuery query) {
        // 1. 构建分页对象
        Page<SysCategory> page = new Page<>(query.getPageNum(), query.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SysCategory> wrapper = new LambdaQueryWrapper<>();

        // 分类名称 模糊查询
        if (query.getCategoryName() != null && !query.getCategoryName().isEmpty()) {
            wrapper.like(SysCategory::getCategoryName, query.getCategoryName());
        }

        // 父分类ID 精确查询
        if (query.getPCategoryId() != null && !query.getPCategoryId().isEmpty()) {
            wrapper.eq(SysCategory::getPCategoryId, query.getPCategoryId());
        }

        // 3. 查询并返回
        return sysCategoryMapper.selectPage(page, wrapper);
    }

    /**
     * 树形结构分页查询
     */
    @Override
    public IPage<SysCategoryTreeVO> findTreeListByParam(SysCategoryQuery query) {
        // 1. 如果传入了 categoryName，需要找到匹配的分类及其所有子分类
        List<SysCategory> allCategories;
        if (query.getCategoryName() != null && !query.getCategoryName().isEmpty()) {
            // 先查询所有分类
            LambdaQueryWrapper<SysCategory> allWrapper = new LambdaQueryWrapper<>();
            allWrapper.orderByAsc(SysCategory::getSort);
            List<SysCategory> allCats = sysCategoryMapper.selectList(allWrapper);
            
            // 找到名称匹配的分类ID
            List<String> matchedCategoryIds = allCats.stream()
                    .filter(cat -> cat.getCategoryName() != null && 
                            cat.getCategoryName().contains(query.getCategoryName()))
                    .map(SysCategory::getCategoryId)
                    .collect(Collectors.toList());
            
            // 收集匹配分类及其所有子分类的ID
            List<String> relevantCategoryIds = new ArrayList<>();
            for (String categoryId : matchedCategoryIds) {
                collectAllRelatedCategoryIds(categoryId, allCats, relevantCategoryIds);
            }
            
            // 过滤出相关分类
            allCategories = allCats.stream()
                    .filter(cat -> relevantCategoryIds.contains(cat.getCategoryId()))
                    .collect(Collectors.toList());
        } else {
            // 没有传入 categoryName，按原有逻辑处理
            LambdaQueryWrapper<SysCategory> wrapper = new LambdaQueryWrapper<>();
            
            // 父分类ID 精确查询
            if (query.getPCategoryId() != null && !query.getPCategoryId().isEmpty()) {
                wrapper.eq(SysCategory::getPCategoryId, query.getPCategoryId());
            }
            
            // 按排序字段排序
            wrapper.orderByAsc(SysCategory::getSort);
            
            allCategories = sysCategoryMapper.selectList(wrapper);
        }
        
        // 2. 转换为树形结构VO
        List<SysCategoryTreeVO> treeList = buildTree(allCategories);
        
        // 3. 手动分页
        int pageNum = query.getPageNum();
        int pageSize = query.getPageSize();
        int total = treeList.size();
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, total);
        
        List<SysCategoryTreeVO> pageList = new ArrayList<>();
        if (fromIndex < total) {
            pageList = treeList.subList(fromIndex, toIndex);
        }
        
        // 4. 构建分页结果
        Page<SysCategoryTreeVO> page = new Page<>(pageNum, pageSize, total);
        page.setRecords(pageList);
        
        return page;
    }
    
    /**
     * 构建树形结构
     */
    private List<SysCategoryTreeVO> buildTree(List<SysCategory> categories) {
        // 转换为VO列表
        List<SysCategoryTreeVO> allNodes = categories.stream()
                .map(this::convertToTreeVO)
                .collect(Collectors.toList());
        
        // 找出所有根节点（pCategoryId为0或null的）
        List<SysCategoryTreeVO> rootNodes = allNodes.stream()
                .filter(node -> node.getPCategoryId() == null || "0".equals(node.getPCategoryId()))
                .collect(Collectors.toList());
        
        // 为每个根节点设置子节点
        for (SysCategoryTreeVO rootNode : rootNodes) {
            setChildren(rootNode, allNodes, 0);
        }
        
        return rootNodes;
    }
    
    /**
     * 递归设置子节点
     */
    private void setChildren(SysCategoryTreeVO parent, List<SysCategoryTreeVO> allNodes, int level) {
        List<SysCategoryTreeVO> children = allNodes.stream()
                .filter(node -> parent.getCategoryId().equals(node.getPCategoryId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            parent.setLevel(level + 1);
            parent.setChildren(children);
            
            // 递归设置子节点的子节点
            for (SysCategoryTreeVO child : children) {
                setChildren(child, allNodes, level + 1);
            }
        } else {
            parent.setLevel(level);
            parent.setChildren(new ArrayList<>());
        }
    }
    
    /**
     * 转换实体为树形VO
     */
    private SysCategoryTreeVO convertToTreeVO(SysCategory category) {
        SysCategoryTreeVO vo = new SysCategoryTreeVO();
        BeanUtils.copyProperties(category, vo);
        vo.setLevel(0); // 默认层级，后续会重新计算
        vo.setChildren(new ArrayList<>());
        


        return vo;
    }
    /**
     * 查询所有分类列表（用于下拉选择）
     */
    @Override
    public List<SysCategory> listAllCategories() {
        LambdaQueryWrapper<SysCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysCategory::getSort);
        return sysCategoryMapper.selectList(wrapper);
    }
    
    @Override
    public void add(SysCategory bean) {


        if (bean.getCategoryId() != null && !bean.getCategoryId().isEmpty()) {
            int idLength = bean.getCategoryId().length();
            System.out.println("  自定义categoryId长度: " + idLength);
            if (idLength > 20) {
                throw new RuntimeException("categoryId长度不能超过20位，当前长度: " + idLength);
            }
            // 检查是否已存在
            SysCategory existCategory = sysCategoryMapper.selectById(bean.getCategoryId());
            if (existCategory != null) {
                throw new RuntimeException("分类ID已存在: " + bean.getCategoryId());
            }
        } else {
            // 自动生成17位短ID（时间戳13位+4位随机数）
            String newId = generateShortId();
            bean.setCategoryId(newId);
            System.out.println("  自动生成categoryId: " + newId + " (长度: " + newId.length() + ")");
        }
        
        // 验证父分类是否存在
        if (bean.getPCategoryId() != null && !"0".equals(bean.getPCategoryId())) {
            SysCategory parent = sysCategoryMapper.selectById(bean.getPCategoryId());
            if (parent == null) {
                throw new RuntimeException("父分类不存在，父分类ID: " + bean.getPCategoryId());
            }
            System.out.println("  父分类验证通过: " + parent.getCategoryName());
        }
        
        // 设置默认值
        if(bean.getSort() == null){
            bean.setSort(0);
        }
        
        try {
            sysCategoryMapper.insert(bean);
            System.out.println("✅ 分类添加成功: " + bean.getCategoryId());
            System.out.println("============================\n");
        } catch (Exception e) {
            System.err.println("❌ 添加分类失败: " + e.getMessage());
            System.err.println("失败的数据: categoryId=" + bean.getCategoryId() + 
                             ", categoryName=" + bean.getCategoryName());
            throw new RuntimeException("添加分类失败: " + e.getMessage(), e);
        }
    }

//     @Override
//     public void addBatch(List<SysCategory> listBean) {
//         if (listBean == null || listBean.size() == 0) {
//             return;
//         }
//         // MP自带批量插入
//         this.saveBatch(listBean);
//     }
    @Override
    public SysCategory getSysCategoryByCategoryId(String categoryId) {
        return sysCategoryMapper.selectById(categoryId);
    }
    @Override
    public void updateSysCategoryByCategoryId(SysCategory bean, String categoryId) {
        // 验证分类是否存在
        SysCategory existCategory = sysCategoryMapper.selectById(categoryId);


        if (existCategory == null) {
            throw new RuntimeException("分类不存在");
        }

        // 验证父分类是否存在（不能将自己设为父分类）
        if (bean.getPCategoryId() != null && !"0".equals(bean.getPCategoryId())) {
            if (bean.getPCategoryId().equals(categoryId)) {
                throw new RuntimeException("不能将自己设为父分类");
            }
            SysCategory parent = sysCategoryMapper.selectById(bean.getPCategoryId());
            if (parent == null) {
                throw new RuntimeException("父分类不存在");
            }
        }

        // 把前端所有修改的字段都赋值进去
        if (bean.getCategoryName() != null) {
            existCategory.setCategoryName(bean.getCategoryName());
        }
        if (bean.getPCategoryId() != null) {
            existCategory.setPCategoryId(bean.getPCategoryId());
        }
        if (bean.getSort() != null) {
            existCategory.setSort(bean.getSort());
        }


        // 直接更新查询出来的完整对象，不会出现全字段null
        sysCategoryMapper.updateById(existCategory);
    }
    /**
     * 删除分类（级联删除子分类）
     */
    @Override
    public void deleteSysCategoryByCategoryId(String categoryId) {
        // 验证分类是否存在
        SysCategory category = sysCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        
        // 递归查找所有子分类ID
        List<String> allCategoryIds = new ArrayList<>();
        allCategoryIds.add(categoryId);
        collectChildCategoryIds(categoryId, allCategoryIds);
        
        // 批量删除
        sysCategoryMapper.deleteBatchIds(allCategoryIds);
    }
    
    /**
     * 批量删除分类
     */
    @Override
    public void deleteBatch(List<String> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            return;
        }
        
        // 收集所有需要删除的ID（包括子分类）
        List<String> allCategoryIds = new ArrayList<>();
        for (String categoryId : categoryIds) {
            if (!allCategoryIds.contains(categoryId)) {
                allCategoryIds.add(categoryId);
                collectChildCategoryIds(categoryId, allCategoryIds);
            }
        }
        
        // 批量删除
        sysCategoryMapper.deleteBatchIds(allCategoryIds);
    }
    
    /**
     * 递归收集所有子分类ID
     */
    private void collectChildCategoryIds(String parentCategoryId, List<String> allCategoryIds) {
        LambdaQueryWrapper<SysCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysCategory::getPCategoryId, parentCategoryId);
        List<SysCategory> children = sysCategoryMapper.selectList(wrapper);
        
        for (SysCategory child : children) {
            if (!allCategoryIds.contains(child.getCategoryId())) {
                allCategoryIds.add(child.getCategoryId());
                // 递归查找子分类的子分类
                collectChildCategoryIds(child.getCategoryId(), allCategoryIds);
            }
        }
    }
    
    /**
     * 生成17位短ID（时间戳13位+4位随机数）
     */
    private String generateShortId() {
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 9000) + 1000; // 4位随机数 1000-9999
        return String.valueOf(timestamp) + random;
    }
    
    /**
     * 收集匹配分类及其所有子分类的ID
     */
    private void collectAllRelatedCategoryIds(String categoryId, List<SysCategory> allCategories, List<String> resultIds) {
        if (resultIds.contains(categoryId)) {
            return;
        }
        
        // 添加当前分类ID
        resultIds.add(categoryId);
        
        // 查找当前分类的直接子分类
        List<SysCategory> children = allCategories.stream()
                .filter(cat -> categoryId.equals(cat.getPCategoryId()))
                .collect(Collectors.toList());
        
        // 递归处理子分类
        for (SysCategory child : children) {
            collectAllRelatedCategoryIds(child.getCategoryId(), allCategories, resultIds);
        }
    }

}