package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.SysCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysCategoryMapper extends BaseMapper<SysCategory> {
    /**
     * 查询首页一级分类（p_category_id = '0'，按 sort 升序）
     */
    @Select("SELECT category_id, category_name, p_category_id, sort " +
            "FROM sys_category " +
            "WHERE p_category_id = '0' " +
            "ORDER BY sort ASC " +
            "LIMIT #{limit}")
    List<SysCategory> selectFirstLevel(@org.apache.ibatis.annotations.Param("limit") int limit);
}
