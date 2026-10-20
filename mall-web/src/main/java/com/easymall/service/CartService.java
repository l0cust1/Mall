package com.easymall.service;

import com.easymall.entity.DTO.CartAddDTO;
import com.easymall.entity.DTO.CartQuantityDTO;
import com.easymall.entity.DTO.CartSelectAllDTO;
import com.easymall.entity.DTO.CartSelectDTO;
import com.easymall.entity.VO.CartSummaryVO;

/**
 * 购物车服务
 */
public interface CartService {

    /**
     * 加入购物车，同一用户同一 SKU 自动合并数量
     */
    void addItem(Long userId, CartAddDTO dto);

    /**
     * 查询购物车列表及汇总信息
     */
    CartSummaryVO listItems(Long userId);

    /**
     * 修改购物车项数量
     */
    void updateQuantity(Long userId, CartQuantityDTO dto);

    /**
     * 勾选/取消勾选单个购物车项
     */
    void updateSelected(Long userId, CartSelectDTO dto);

    /**
     * 全选/取消全选
     */
    void selectAll(Long userId, CartSelectAllDTO dto);

    /**
     * 删除单个购物车项
     */
    void removeItem(Long userId, Long cartItemId);

    /**
     * 清空当前用户购物车
     */
    void clear(Long userId);

    /**
     * 购物车商品数量合计（用于导航栏角标）
     */
    int countItems(Long userId);
}
