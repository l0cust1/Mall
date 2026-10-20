package com.easymall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.CartItem;
import com.easymall.entity.DTO.CartAddDTO;
import com.easymall.entity.DTO.CartQuantityDTO;
import com.easymall.entity.DTO.CartSelectAllDTO;
import com.easymall.entity.DTO.CartSelectDTO;
import com.easymall.entity.ProductSku;
import com.easymall.entity.VO.CartItemVO;
import com.easymall.entity.VO.CartSummaryVO;
import com.easymall.entity.constants.ProductBase;
import com.easymall.mapper.CartItemMapper;
import com.easymall.mapper.ProductBaseMapper;
import com.easymall.mapper.ProductSkuMapper;
import com.easymall.service.CartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 购物车服务实现
 */
@Service
@Slf4j
public class CartServiceImpl extends ServiceImpl<CartItemMapper, CartItem> implements CartService {

    private final ProductSkuMapper productSkuMapper;
    private final ProductBaseMapper productBaseMapper;

    public CartServiceImpl(ProductSkuMapper productSkuMapper, ProductBaseMapper productBaseMapper) {
        this.productSkuMapper = productSkuMapper;
        this.productBaseMapper = productBaseMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addItem(Long userId, CartAddDTO dto) {
        if (dto.getSkuId() == null && StrUtil.isBlank(dto.getPropertyValueIdHash())) {
            throw new RuntimeException("请选择商品规格");
        }

        ProductSku sku = resolveSku(dto);
        if (StrUtil.isBlank(sku.getPropertyValueIdHash())) {
            throw new RuntimeException("规格标识缺失，无法加入购物车");
        }

        ProductBase product = productBaseMapper.selectById(dto.getProductId());
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        if (!Integer.valueOf(1).equals(product.getStatus())) {
            throw new RuntimeException("商品已下架");
        }
        if (!product.getProductId().equals(sku.getProductId())) {
            throw new RuntimeException("商品与规格不匹配");
        }
        if (sku.getStock() == null || sku.getStock() < dto.getQuantity()) {
            throw new RuntimeException("库存不足，仅剩 " + (sku.getStock() == null ? 0 : sku.getStock()) + " 件");
        }

        CartItem existing = this.getOne(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .eq(CartItem::getSkuId, sku.getId()));

        if (existing != null) {
            int currentQuantity = existing.getQuantity() == null ? 0 : existing.getQuantity();
            int newQuantity = currentQuantity + dto.getQuantity();
            if (sku.getStock() < newQuantity) {
                int canAdd = sku.getStock() - currentQuantity;
                throw new RuntimeException("库存不足，购物车已有 " + currentQuantity
                        + " 件，最多还能加 " + Math.max(canAdd, 0) + " 件");
            }
            existing.setQuantity(newQuantity);
            existing.setSelected(1);
            existing.setUpdateTime(LocalDateTime.now());
            this.updateById(existing);
            return;
        }

        CartItem item = new CartItem();
        item.setUserId(userId);
        item.setProductId(product.getProductId());
        item.setSkuId(sku.getId());
        item.setPropertyValueIdHash(sku.getPropertyValueIdHash());
        item.setQuantity(dto.getQuantity());
        item.setSelected(1);
        this.save(item);
    }

    @Override
    public CartSummaryVO listItems(Long userId) {
        List<CartItem> cartItems = this.list(
                new LambdaQueryWrapper<CartItem>()
                        .eq(CartItem::getUserId, userId)
                        .orderByDesc(CartItem::getUpdateTime)
                        .orderByDesc(CartItem::getId));

        CartSummaryVO summary = new CartSummaryVO();
        if (cartItems.isEmpty()) {
            summary.setItems(Collections.emptyList());
            summary.setTotalQuantity(0);
            summary.setSelectedQuantity(0);
            summary.setSelectedItemCount(0);
            summary.setSelectedAmount(BigDecimal.ZERO);
            summary.setAllSelected(false);
            return summary;
        }

        Set<Long> skuIds = cartItems.stream()
                .map(CartItem::getSkuId)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        Set<String> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

        Map<Long, ProductSku> skuMap = skuIds.isEmpty()
                ? Collections.emptyMap()
                : productSkuMapper.selectBatchIds(skuIds).stream()
                        .collect(Collectors.toMap(ProductSku::getId, sku -> sku, (a, b) -> a));
        Map<String, ProductBase> productMap = productIds.isEmpty()
                ? Collections.emptyMap()
                : productBaseMapper.selectBatchIds(productIds).stream()
                        .collect(Collectors.toMap(ProductBase::getProductId, product -> product, (a, b) -> a));

        List<CartItemVO> voList = new ArrayList<>(cartItems.size());
        int totalQuantity = 0;
        int selectedQuantity = 0;
        int selectedItemCount = 0;
        int validCount = 0;
        int validSelectedCount = 0;
        BigDecimal selectedAmount = BigDecimal.ZERO;

        for (CartItem item : cartItems) {
            int quantity = item.getQuantity() == null ? 0 : item.getQuantity();
            totalQuantity += quantity;

            ProductSku sku = skuMap.get(item.getSkuId());
            ProductBase product = productMap.get(item.getProductId());

            CartItemVO vo = new CartItemVO();
            vo.setCartItemId(item.getId());
            vo.setProductId(item.getProductId());
            vo.setSkuId(item.getSkuId());
            vo.setPropertyValueIdHash(item.getPropertyValueIdHash());
            vo.setQuantity(quantity);
            vo.setSelected(item.getSelected() == null ? 0 : item.getSelected());
            vo.setInvalid(false);

            if (product == null) {
                vo.setProductName("商品已失效");
                vo.setInvalid(true);
                vo.setInvalidReason("商品已失效");
            } else {
                vo.setProductName(product.getProductName());
                vo.setCover(product.getCover());
                if (!Integer.valueOf(1).equals(product.getStatus())) {
                    vo.setInvalid(true);
                    vo.setInvalidReason("商品已下架");
                }
            }

            if (sku == null) {
                vo.setInvalid(true);
                vo.setInvalidReason("规格已失效");
                vo.setPrice(BigDecimal.ZERO);
                vo.setStock(0);
                vo.setSubtotal(BigDecimal.ZERO);
            } else {
                BigDecimal price = sku.getPrice() == null ? BigDecimal.ZERO : sku.getPrice();
                int stock = sku.getStock() == null ? 0 : sku.getStock();
                vo.setSkuInfo(sku.getPropertyValueIds());
                vo.setPrice(price);
                vo.setStock(stock);
                vo.setSubtotal(price.multiply(BigDecimal.valueOf(quantity)));

                if (stock <= 0) {
                    vo.setInvalid(true);
                    vo.setInvalidReason("商品已售罄");
                } else if (stock < quantity) {
                    vo.setInvalid(true);
                    vo.setInvalidReason("库存不足，仅剩 " + stock + " 件");
                }
            }

            boolean selected = Integer.valueOf(1).equals(item.getSelected());
            if (!vo.getInvalid()) {
                validCount++;
                if (selected) {
                    validSelectedCount++;
                    selectedItemCount++;
                    selectedQuantity += quantity;
                    selectedAmount = selectedAmount.add(vo.getSubtotal());
                }
            }

            voList.add(vo);
        }

        summary.setItems(voList);
        summary.setTotalQuantity(totalQuantity);
        summary.setSelectedQuantity(selectedQuantity);
        summary.setSelectedItemCount(selectedItemCount);
        summary.setSelectedAmount(selectedAmount);
        summary.setAllSelected(validCount > 0 && validCount == validSelectedCount);
        return summary;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long userId, CartQuantityDTO dto) {
        CartItem item = requireOwnedItem(userId, dto.getCartItemId());
        ProductSku sku = productSkuMapper.selectById(item.getSkuId());
        if (sku == null) {
            throw new RuntimeException("规格已失效，请删除后重新选择");
        }
        if (sku.getStock() == null || sku.getStock() < dto.getQuantity()) {
            throw new RuntimeException("库存不足，仅剩 " + (sku.getStock() == null ? 0 : sku.getStock()) + " 件");
        }

        item.setQuantity(dto.getQuantity());
        item.setUpdateTime(LocalDateTime.now());
        this.updateById(item);
    }

    @Override
    public void updateSelected(Long userId, CartSelectDTO dto) {
        CartItem item = requireOwnedItem(userId, dto.getCartItemId());
        item.setSelected(Boolean.TRUE.equals(dto.getSelected()) ? 1 : 0);
        item.setUpdateTime(LocalDateTime.now());
        this.updateById(item);
    }

    @Override
    public void selectAll(Long userId, CartSelectAllDTO dto) {
        this.update(new LambdaUpdateWrapper<CartItem>()
                .eq(CartItem::getUserId, userId)
                .set(CartItem::getSelected, Boolean.TRUE.equals(dto.getSelected()) ? 1 : 0)
                .set(CartItem::getUpdateTime, LocalDateTime.now()));
    }

    @Override
    public void removeItem(Long userId, Long cartItemId) {
        boolean removed = this.remove(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, cartItemId)
                .eq(CartItem::getUserId, userId));
        if (!removed) {
            throw new RuntimeException("购物车项不存在");
        }
    }

    @Override
    public void clear(Long userId) {
        this.remove(new LambdaQueryWrapper<CartItem>().eq(CartItem::getUserId, userId));
    }

    @Override
    public int countItems(Long userId) {
        Integer count = this.baseMapper.sumQuantityByUserId(userId);
        return count == null ? 0 : count;
    }

    private CartItem requireOwnedItem(Long userId, Long cartItemId) {
        CartItem item = this.getOne(new LambdaQueryWrapper<CartItem>()
                .eq(CartItem::getId, cartItemId)
                .eq(CartItem::getUserId, userId));
        if (item == null) {
            throw new RuntimeException("购物车项不存在");
        }
        return item;
    }

    private ProductSku resolveSku(CartAddDTO dto) {
        ProductSku sku = null;
        if (dto.getSkuId() != null) {
            sku = productSkuMapper.selectById(dto.getSkuId());
        } else if (StrUtil.isNotBlank(dto.getPropertyValueIdHash())) {
            sku = productSkuMapper.selectByHash(dto.getPropertyValueIdHash());
        }
        if (sku == null) {
            throw new RuntimeException("商品规格不存在");
        }
        return sku;
    }
}
