package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.DTO.ProductPublishDTO;
import com.easymall.entity.DTO.UpdateStockDTO;
import com.easymall.entity.ProductBase;
import com.easymall.entity.ProductInfoQuery;
import com.easymall.entity.ProductProperty;
import com.easymall.entity.ProductSku;
import com.easymall.entity.vo.ModifyStockVO;
import com.easymall.entity.vo.PriceVO;
import com.easymall.entity.vo.ProductListVO;
import com.easymall.mapper.ProductBaseMapper;
import com.easymall.mapper.ProductPropertyMapper;
import com.easymall.mapper.ProductSkuMapper;
import com.easymall.service.ProductService;
import com.easymall.utils.StringTooles;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductBaseMapper, ProductBase> implements ProductService {

    @Resource
    private ProductBaseMapper productBaseMapper;
    
    @Resource
    private ProductSkuMapper productSkuMapper;
    
    @Resource
    private ProductPropertyMapper productPropertyMapper;

    @Override
    public IPage<ProductListVO> getPageByParam(ProductInfoQuery query) {
        Page<ProductListVO> page = new Page<>(query.getPageNum(), query.getPageSize());
        return productBaseMapper.selectProductList(page, query.getProductName(),  query.getStatus(), query.getCommendType());
    }
    
    /**
     * 删除商品及其关联的SKU信息
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        
        // 先删除关联的SKU信息
        productSkuMapper.delete(new LambdaUpdateWrapper<ProductSku>()
                .eq(ProductSku::getProductId, productId));
        productPropertyMapper.delete(new LambdaUpdateWrapper<ProductProperty>()
                .eq(ProductProperty::getProductId, productId));
        
        // 再删除商品基本信息
        int result = productBaseMapper.deleteById(productId);
        if (result == 0) {
            throw new RuntimeException("商品不存在或删除失败");
        }
    }
    
    /**
     * 修改商品推荐状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCommendType(String productId, Integer commendType) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (commendType == null || (commendType != 0 && commendType != 1)) {
            throw new IllegalArgumentException("推荐类型无效，只能为0或1");
        }
        
        ProductBase product = productBaseMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        product.setCommendType(commendType);
        productBaseMapper.updateById(product);
    }
    /**
     * 修改商品价格区间
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePrice(String productId, PriceVO priceVO) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (priceVO == null) {
            throw new IllegalArgumentException("价格数据不能为空");
        }
        if (priceVO.getMinPrice() == null || priceVO.getMaxPrice() == null) {
            throw new IllegalArgumentException("最低价和最高价不能为空");
        }
        if (priceVO.getMinPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("最低价不能为负数");
        }
        if (priceVO.getMaxPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("最高价不能为负数");
        }
        if (priceVO.getMaxPrice().compareTo(priceVO.getMinPrice()) < 0) {
            throw new IllegalArgumentException("最高价不能小于最低价");
        }

        ProductBase product = productBaseMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        product.setMinPrice(priceVO.getMinPrice());
        product.setMaxPrice(priceVO.getMaxPrice());
        productBaseMapper.updateById(product);
    }

    /**
     * 修改商品上下架状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(String productId, Integer status) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态无效，只能为0或1");
        }
        
        ProductBase product = productBaseMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        
        product.setStatus(status);
        productBaseMapper.updateById(product);
    }
    
    /**
     * 批量修改商品SKU库存
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStock(UpdateStockDTO dto) {
        if (dto == null || dto.getProductId() == null || dto.getProductId().trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (dto.getSkus() == null || dto.getSkus().isEmpty()) {
            throw new IllegalArgumentException("SKU库存数据不能为空");
        }

        for (UpdateStockDTO.SkuStock skuStock : dto.getSkus()) {
            if (skuStock.getSkuId() == null) {
                throw new IllegalArgumentException("SKU ID不能为空");
            }
            if (skuStock.getStock() == null || skuStock.getStock() < 0) {
                throw new IllegalArgumentException("SKU库存数量不能为负数");
            }

            ProductSku sku = productSkuMapper.selectById(skuStock.getSkuId());
            if (sku == null) {
                throw new RuntimeException("SKU不存在: " + skuStock.getSkuId());
            }
            if (!dto.getProductId().equals(sku.getProductId())) {
                throw new RuntimeException("SKU不属于该商品: " + skuStock.getSkuId());
            }

            sku.setStock(skuStock.getStock());
            productSkuMapper.updateById(sku);
        }
    }
    @Override
    public void updateRemark(String productId, String remark) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }
        if (remark == null || remark.trim().isEmpty()) {
            throw new IllegalArgumentException("商品备注不能为空");
        }

        ProductBase product = productBaseMapper.selectById(productId);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }

        product.setRemark(remark);
        productBaseMapper.updateById(product);
    }
    /**
     * 发布商品（新增或编辑）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String publishProduct(ProductPublishDTO publishDTO) {
        // 1. 参数验证
        if (publishDTO == null) {
            throw new IllegalArgumentException("商品信息不能为空");
        }
        if (publishDTO.getProductName() == null || publishDTO.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("商品名称不能为空");
        }
        if (publishDTO.getCategoryId() == null || publishDTO.getCategoryId().trim().isEmpty()) {
            throw new IllegalArgumentException("商品分类不能为空");
        }
        if (publishDTO.getSkus() == null || publishDTO.getSkus().isEmpty()) {
            throw new IllegalArgumentException("至少需要添加一个SKU");
        }

        String productId;

//            // 2. 保存或更新商品基本信息
//            if (publishDTO.getProductId() != null && !publishDTO.getProductId().trim().isEmpty()) {
//                // 编辑模式
//                productId = publishDTO.getProductId();
//                ProductBase existProduct = productBaseMapper.selectById(productId);
//                if (existProduct == null) {
//                    throw new RuntimeException("商品不存在");
//                }
//
//                // 更新商品基本信息
//                existProduct.setProductName(publishDTO.getProductName());
//                existProduct.setProductDesc(publishDTO.getProductDesc());
//                existProduct.setCover(publishDTO.getCover());
//                existProduct.setCategoryId(publishDTO.getCategoryId());
//                existProduct.setPCategoryId(publishDTO.getPCategoryId());
//                existProduct.setStatus(publishDTO.getStatus() != null ? publishDTO.getStatus() : 0);
//                existProduct.setCommendType(publishDTO.getCommendType() != null ? publishDTO.getCommendType() : 0);
//                existProduct.setRemark(publishDTO.getRemark());
//
//                productBaseMapper.updateById(existProduct);
//
//                // 删除旧的SKU信息
//                productSkuMapper.delete(new LambdaUpdateWrapper<ProductSku>()
//                        .eq(ProductSku::getProductId, productId));
//            } else {
            // 新增模式
            productId = StringTooles.getRandomString(20);

            ProductBase productBase = new ProductBase();
            productBase.setProductId(productId);
            productBase.setProductName(publishDTO.getProductName());
            productBase.setProductDesc(publishDTO.getProductDesc());
            productBase.setCover(publishDTO.getCover());
            productBase.setCategoryId(publishDTO.getCategoryId());
            productBase.setPCategoryId(publishDTO.getPCategoryId());
            productBase.setStatus(publishDTO.getStatus() != null ? publishDTO.getStatus() : 0);
            productBase.setCommendType(publishDTO.getCommendType() != null ? publishDTO.getCommendType() : 0);
            productBase.setRemark(publishDTO.getRemark());
            productBase.setCreateTime(LocalDateTime.now());
            productBase.setTotalSale(0);

            productBaseMapper.insert(productBase);


        // 3. 保存商品属性（尺码/颜色/段位等）
        if (publishDTO.getProperties() != null && !publishDTO.getProperties().isEmpty()) {
            for (ProductPublishDTO.ProductPropertyDTO propertyDTO : publishDTO.getProperties()) {
                if (propertyDTO.getPropertyValues() != null && !propertyDTO.getPropertyValues().isEmpty()) {
                    for (ProductPublishDTO.ProductPropertyDTO.ProductPropertyValueDTO valueDTO : propertyDTO.getPropertyValues()) {
                        ProductProperty property = new ProductProperty();
                        property.setProductId(productId);
                        property.setPropertyName(propertyDTO.getPropertyName());
                        property.setPropertySort(propertyDTO.getPropertySort());
                        property.setPropertyValue(valueDTO.getPropertyValue());
                        property.setPropertyRemark(valueDTO.getPropertyRemark());
                        property.setSort(valueDTO.getSort());
                        
                        productPropertyMapper.insert(property);
                    }
                }
            }
        }

        // 4. 保存SKU信息
        List<ProductSku> skuList = new ArrayList<>();
        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        int totalStock = 0;

        for (int i = 0; i < publishDTO.getSkus().size(); i++) {
            ProductPublishDTO.ProductSkuDTO skuDTO = publishDTO.getSkus().get(i);

            if (skuDTO.getPrice() == null || skuDTO.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("SKU价格不能为空且不能为负数");
            }
            if (skuDTO.getStock() == null || skuDTO.getStock() < 0) {
                throw new IllegalArgumentException("SKU库存不能为负数");
            }

            ProductSku sku = new ProductSku();
            sku.setProductId(productId);
            sku.setPropertyValueIds(skuDTO.getPropertyValueIds());
            sku.setPropertyValueIdHash(StringTooles.getRandomString(20));
            sku.setPrice(skuDTO.getPrice());
            sku.setStock(skuDTO.getStock());
            sku.setSort(skuDTO.getSort() != null ? skuDTO.getSort() : i);

            skuList.add(sku);

            // 统计价格区间
            if (minPrice == null || skuDTO.getPrice().compareTo(minPrice) < 0) {
                minPrice = skuDTO.getPrice();
            }
            if (maxPrice == null || skuDTO.getPrice().compareTo(maxPrice) > 0) {
                maxPrice = skuDTO.getPrice();
            }

            // 统计总库存
            totalStock += skuDTO.getStock();
        }

        // 批量插入SKU
        for (ProductSku sku : skuList) {
            productSkuMapper.insert(sku);
        }

        // 5. 更新商品的价格和库存信息
        ProductBase product = productBaseMapper.selectById(productId);
        product.setMinPrice(minPrice);
        product.setMaxPrice(maxPrice);

        productBaseMapper.updateById(product);

        return productId;
    }
//    ProductBase  getProductById(String productId);@Transactional
    public ProductBase getProductById(String productId) {
        return productBaseMapper.selectById(productId);
    }

    @Override
    public List<ModifyStockVO> loadStock(String productId) {
        if (productId == null || productId.trim().isEmpty()) {
            throw new IllegalArgumentException("商品ID不能为空");
        }

        List<ProductSku> skuList = productSkuMapper.selectSkusByProductId(productId);
        if (skuList == null || skuList.isEmpty()) {
            throw new RuntimeException("商品不存在或没有SKU信息");
        }

        return skuList.stream().map(sku -> {
            ModifyStockVO vo = new ModifyStockVO();
            vo.setSkuId(sku.getId());
            vo.setProductId(sku.getProductId());
            vo.setStock(sku.getStock());
            vo.setPropertyValueIds(sku.getPropertyValueIds());
            return vo;
        }).collect(Collectors.toList());
    }
}
