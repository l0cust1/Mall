package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.RecommendProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RecommendProductMapper extends BaseMapper<RecommendProduct> {

    /**
     * 根据推荐类型查询启用状态的推荐商品
     */
    @Select("SELECT id, product_id, product_name, product_image, " +
            "price, original_price, recommend_type, sort_order " +
            "FROM recommend_product " +
            "WHERE status = 1 AND recommend_type = #{type} " +
            "ORDER BY sort_order ASC " +
            "LIMIT #{limit}")
    List<RecommendProduct> selectByType(@Param("type") Integer type, @Param("limit") int limit);
}