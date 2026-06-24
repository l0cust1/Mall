package com.easymall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.DTO.ProductPublishDTO;
import com.easymall.entity.DTO.UpdateStockDTO;
import com.easymall.entity.ProductBase;
import com.easymall.entity.ProductInfoQuery;
import com.easymall.entity.vo.ModifyStockVO;
import com.easymall.entity.vo.ProductListVO;

import java.util.List;

public interface ProductService extends IService<ProductBase> {
    IPage<ProductListVO> getPageByParam(ProductInfoQuery query);
    
    /**
     * 发布商品（新增或编辑）
     * @param publishDTO 商品发布信息
     * @return 商品ID
     */
    String publishProduct(ProductPublishDTO publishDTO);
    
    /**
     * 删除商品及其关联的SKU信息
     * @param productId 商品ID
     */
    void deleteProduct(String productId);

    /**
     * 加载商品的所有SKU库存数据
     * @param productId 商品ID
     * @return SKU库存列表
     */
    List<ModifyStockVO> loadStock(String productId);

    ProductBase  getProductById(String productId);
    /**
     * 修改商品推荐状态
     * @param productId 商品ID
     * @param commendType 推荐类型：0-未推荐，1-已推荐
     */
    void updateCommendType(String productId, Integer commendType);
    
    /**
     * 修改商品上下架状态
     * @param productId 商品ID
     * @param status 状态：0-下架，1-上架
     */
    void updateStatus(String productId, Integer status);



    /**
     * 批量修改商品SKU库存
     * @param dto 库存更新数据
     */
    void updateStock(UpdateStockDTO dto);

    void updateRemark(String productId, String remark);
}
