package com.easymall.service.impl;

import com.easymall.entity.ProductSku;
import com.easymall.entity.VO.OrderConfirmVO;
import com.easymall.entity.VO.ProductDetailVO;
import com.easymall.entity.constants.ProductBase;
import com.easymall.mapper.ProductBaseMapper;
import com.easymall.mapper.ProductSkuMapper;
import com.easymall.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductBaseMapper productBaseMapper;

    @Autowired
    private ProductSkuMapper productSkuMapper;

    @Override
    public ProductDetailVO getProductDetail(String productId) {
        // 1. 查询商品基础信息
        ProductBase productBase = productBaseMapper.selectByProductId(productId);
        if (productBase == null) {
            log.warn("商品不存在，productId: {}", productId);
            return null;
        }

        // 2. 查询SKU列表
        List<ProductSku> skuList = productSkuMapper.selectSkusByProductId(productId);
        if (CollectionUtils.isEmpty(skuList)) {
            log.info("商品无SKU数据，productId: {}", productId);
        }

        // 3. 组装VO
        ProductDetailVO vo = new ProductDetailVO();
        BeanUtils.copyProperties(productBase, vo);

        List<ProductDetailVO.ProductSkuVO> skuVOList = skuList.stream()
                .map(sku -> {
                    ProductDetailVO.ProductSkuVO skuVO = new ProductDetailVO.ProductSkuVO();
                    BeanUtils.copyProperties(sku, skuVO);
                    return skuVO;
                })
                .collect(Collectors.toList());
        vo.setSkus(skuVOList);

        return vo;
    }

    @Override
    public OrderConfirmVO getOrderConfirmInfo(String property_value_id_hash) {
        // 1. 根据 property_value_id_hash 查询SKU信息
        ProductSku productSku = productSkuMapper.selectOne(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductSku>()
                        .eq(ProductSku::getPropertyValueIdHash, property_value_id_hash)
        );
        if (productSku == null) {
            log.warn("SKU不存在，hash: {}", property_value_id_hash);
            throw new RuntimeException("SKU不存在");
        }

        // 2. 根据product_id查询商品基础信息
        ProductBase productBase = productBaseMapper.selectByProductId(productSku.getProductId());
        if (productBase == null) {
            log.warn("商品不存在，productId: {}", productSku.getProductId());
            throw new RuntimeException("商品不存在");
        }

        // 3. 校验商品状态（防止已下架商品被下单）
        if (productBase.getStatus() == null || productBase.getStatus() != 1) {
            log.warn("商品已下架，productId: {}", productBase.getProductId());
            throw new RuntimeException("商品已下架");
        }

        // 4. 校验库存（防止库存不足被下单）
        if (productSku.getStock() == null || productSku.getStock() <= 0) {
            log.warn("库存不足，hash: {}, stock: {}", property_value_id_hash, productSku.getStock());
            throw new RuntimeException("库存不足");
        }

        // 5. 组装订单确认信息
        OrderConfirmVO vo = new OrderConfirmVO();
        vo.setProductId(productBase.getProductId());
        vo.setProductName(productBase.getProductName());
        vo.setCover(productBase.getCover());
        vo.setPropertyValueIdHash(productSku.getPropertyValueIdHash());
        vo.setPropertyValueIds(productSku.getPropertyValueIds());
        vo.setPrice(productSku.getPrice());

        return vo;
    }
}
