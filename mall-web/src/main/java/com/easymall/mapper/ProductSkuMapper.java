package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.easymall.entity.ProductSku;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    /**
     * 根据商品ID查询所有SKU列表
     */
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId} ORDER BY sort ASC, id ASC")
    List<ProductSku> selectSkusByProductId(@Param("productId") String productId);

    default ProductSku selectByHash(String hash) {
        return selectOne(Wrappers.lambdaQuery(ProductSku.class)
                .eq(ProductSku::getPropertyValueIdHash, hash));
    }



}
