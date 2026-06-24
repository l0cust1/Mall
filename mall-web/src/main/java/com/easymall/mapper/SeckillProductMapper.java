package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.SeckillProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SeckillProductMapper extends BaseMapper<SeckillProduct> {
    // 如果有复杂查询（如查询当前生效的秒杀），可以写自定义方法或使用 @Select
    @Select("SELECT * FROM seckill_product WHERE status = 1 AND start_time <= NOW() AND end_time >= NOW() ORDER BY sort_order ASC LIMIT #{limit}")
    List<SeckillProduct> selectCurrentValid(@Param("limit") int limit);
}