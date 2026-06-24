package com.easymall.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.Carousel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CarouselMapper extends BaseMapper<Carousel> {

    /**
     * 查询首页启用的轮播图（按排序升序，限制条数）
     */
    @Select("SELECT id, title, image_url, link_url, sort_order " +
            "FROM carousel " +
            "WHERE status = 1 " +
            "ORDER BY sort_order ASC " +
            "LIMIT #{limit}")
    List<Carousel> selectHomeList(@org.apache.ibatis.annotations.Param("limit") int limit);
}