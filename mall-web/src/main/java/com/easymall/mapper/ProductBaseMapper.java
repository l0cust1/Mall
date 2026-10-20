package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.easymall.entity.constants.ProductBase;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ProductBaseMapper extends BaseMapper<ProductBase> {



    // default 写在 Mapper 接口内，自定义查询方法
    default ProductBase selectByProductId(String productId) {
        return selectOne(Wrappers.lambdaQuery(ProductBase.class)
                .eq(ProductBase::getProductId, productId));
    }
}