package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easymall.entity.ProductBase;
import com.easymall.entity.vo.ProductListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface ProductBaseMapper extends BaseMapper<ProductBase> {
    @Select("<script>" +
            "SELECT " +
            "pb.*, " +
            "sc.category_name as productCategoryName, " +
            "IFNULL(SUM(ps.stock),0) as totalStock, " + // 总库存（所有SKU库存和）
            "IFNULL(COUNT(ps.id),0) as skuCount " +     // SKU数量
            "FROM product_base pb " +
            "LEFT JOIN sys_category sc ON pb.category_id = sc.category_id " +
            "LEFT JOIN product_sku ps ON pb.product_id = ps.product_id " +
            "WHERE 1=1 " +
            "<if test='productName != null and productName != \"\"'> " +
            "AND pb.product_name LIKE CONCAT('%',#{productName},'%') " +
            "</if> " +
            "<if test='status != null'> " +
            "AND pb.status = #{status} " +
            "</if> " +
            "<if test='commendType != null'> " +
            "AND pb.commend_type = #{commendType} " +
            "</if> " +
            "GROUP BY pb.product_id " +
            "ORDER BY pb.create_time DESC" +
            "</script>")
    IPage<ProductListVO> selectProductList(IPage<ProductListVO> page,
                                           @Param("productName") String productName,
                                           @Param("status") Integer status,
                                           @Param("commendType") Integer commendType);
}