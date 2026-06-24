package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.ProductSku;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.math.BigDecimal;
import java.util.List;

/**
 * 商品SKU Mapper
 */
@Mapper
public interface ProductSkuMapper extends BaseMapper<ProductSku> {

    /**
     * 根据商品ID查询所有SKU
     */
    @Select("SELECT * FROM product_sku WHERE product_id = #{productId} ORDER BY sort ASC")
    List<ProductSku> selectSkusByProductId(@Param("productId") String productId);

    /**
     * 根据商品ID统计总库存
     */
    @Select("SELECT IFNULL(SUM(stock), 0) FROM product_sku WHERE product_id = #{productId}")
    Integer selectTotalStockByProductId(@Param("productId") String productId);

    /**
     * 根据商品ID统计SKU数量
     */
    @Select("SELECT COUNT(*) FROM product_sku WHERE product_id = #{productId}")
    Integer selectSkuCountByProductId(@Param("productId") String productId);

    /**
     * 根据商品ID查询最小价格
     */
    @Select("SELECT IFNULL(MIN(price), 0) FROM product_sku WHERE product_id = #{productId}")
    BigDecimal selectMinPriceByProductId(@Param("productId") String productId);

    /**
     * 根据商品ID查询最大价格
     */
    @Select("SELECT IFNULL(MAX(price), 0) FROM product_sku WHERE product_id = #{productId}")
    BigDecimal selectMaxPriceByProductId(@Param("productId") String productId);

    /**
     * 分页查询SKU列表（关联商品信息）
     */
    @Select("SELECT ps.*, pb.product_name FROM product_sku ps " +
            "LEFT JOIN product_base pb ON ps.product_id = pb.product_id " +
            "WHERE 1=1 " +
            "<if test='productId != null'>AND ps.product_id = #{productId}</if> " +
            "ORDER BY ps.sort ASC")
    IPage<ProductSku> selectSkuPage(Page<ProductSku> page, @Param("productId") String productId);
}