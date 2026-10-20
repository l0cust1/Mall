package com.easymall.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.CartItem;
import com.easymall.entity.DTO.OrderCancelDTO;
import com.easymall.entity.DTO.OrderConfirmReceiveDTO;
import com.easymall.entity.DTO.OrderCreateDTO;
import com.easymall.entity.DTO.OrderSoftDeleteDTO;
import com.easymall.entity.Order.OrderInfo;
import com.easymall.entity.Order.OrderItem;
import com.easymall.entity.OrderLogistics;
import com.easymall.entity.ProductSku;
import com.easymall.entity.UserAddress;
import com.easymall.entity.VO.OrderCreateVO;
import com.easymall.entity.VO.OrderItemVO;
import com.easymall.entity.constants.ProductBase;
import com.easymall.mapper.*;
import com.easymall.service.OrderInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 订单服务实现类
 */
@Service
@Slf4j
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    private static final DateTimeFormatter ORDER_ID_DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final UserAddressMapper userAddressMapper;
    private final ProductSkuMapper productSkuMapper;
    private final ProductBaseMapper productBaseMapper;
    private final OrderItemMapper orderItemMapper;
    private final com.easymall.mapper.OrderLogisticsMapper orderLogisticsMapper;
    private final CartItemMapper cartItemMapper;

    public OrderInfoServiceImpl(UserAddressMapper userAddressMapper,
                                ProductSkuMapper productSkuMapper,
                                ProductBaseMapper productBaseMapper,
                                OrderItemMapper orderItemMapper,
                                com.easymall.mapper.OrderLogisticsMapper orderLogisticsMapper,
                                CartItemMapper cartItemMapper) {
        this.userAddressMapper = userAddressMapper;
        this.productSkuMapper = productSkuMapper;
        this.productBaseMapper = productBaseMapper;
        this.orderItemMapper = orderItemMapper;
        this.orderLogisticsMapper = orderLogisticsMapper;
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCreateVO createOrder(String userId, OrderCreateDTO dto) {
        // ========== 步骤 1：参数校验 ==========

        // 1.1 校验地址是否属于当前用户
        UserAddress address = userAddressMapper.selectById(dto.getAddressId());
        if (address == null) {
            throw new RuntimeException("收货地址不存在");
        }
        if (!address.getUserId().toString().equals(userId)) {
            throw new RuntimeException("收货地址不属于当前用户");
        }

        // 1.2 校验 SKU 库存是否充足，并收集商品信息

        List<SkuStockInfo> skuStockInfoList = new ArrayList<>();

        for (OrderCreateDTO.OrderItemDTO item :  dto.getOrderItemList()) {
            ProductSku sku = productSkuMapper.selectByHash(item.getPropertyValueIdHash());
            if (sku == null) {
                throw new RuntimeException("SKU不存在: " + item.getPropertyValueIdHash());
            }
            if (sku.getStock() == null || sku.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + item.getPropertyValueIdHash()
                        + ", 需要: " + item.getQuantity() + ", 库存: " + sku.getStock());
            }

            // 查询商品基础信息
            ProductBase product = productBaseMapper.selectById(item.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在: " + item.getProductId());
            }
            if (product.getStatus() == null || product.getStatus() != 1) {
                throw new RuntimeException("商品已下架: " + item.getProductId());
            }

            skuStockInfoList.add(new SkuStockInfo(sku, product, item.getQuantity()));
        }

        // ========== 步骤 2：生成订单号、计算订单金额 ==========
        String orderId = generateOrderId();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SkuStockInfo info : skuStockInfoList) {
            BigDecimal subtotal = info.sku.getPrice().multiply(BigDecimal.valueOf(info.quantity));
            totalAmount = totalAmount.add(subtotal);
        }

        // ========== 步骤 3：封装 order_info 订单主表数据 ==========
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderId(orderId);
        orderInfo.setOrderTime(LocalDateTime.now());
        orderInfo.setUserId(userId);
        orderInfo.setBuyerInfo(dto.getBuyerInfo());
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setPayAmount(totalAmount);
        orderInfo.setOrderStatus(1); // 1-待付款
        orderInfo.setBuyerRemark(dto.getBuyerRemark());

        orderInfo.setIsDeleted(0);

        // ========== 步骤 4：插入订单主表 ==========
        this.save(orderInfo);

        // ========== 步骤 5：批量封装并插入 order_item 订单明细表 ==========
        List<OrderItem> orderItems = new ArrayList<>();

        for (SkuStockInfo info : skuStockInfoList) {
            ProductSku sku = info.sku();
            ProductBase product = info.product();
            Integer quantity = info.quantity();

            // 简单判空防护，可以根据业务抛异常
            if (sku == null || product == null) {
                continue;
            }
            BigDecimal price = sku.getPrice() == null ? BigDecimal.ZERO : sku.getPrice();
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setProductId(sku.getProductId());
            orderItem.setProductName(product.getProductName());
            orderItem.setProductCover(product.getCover());
            orderItem.setSkuInfo(sku.getPropertyValueIds());
            orderItem.setPrice(price);
            orderItem.setQuantity(quantity);
            orderItem.setSubtotal(subtotal);

            orderItems.add(orderItem);
        }

        // 批量保存订单明细到数据库
        if (!orderItems.isEmpty()) {
            for (OrderItem item : orderItems) {
                orderItemMapper.insert(item);
            }
        }

        // ========== 步骤 6：保存订单物流信息 ==========
        OrderLogistics logistics = new OrderLogistics();
        logistics.setOrderId(orderId);
        logistics.setReceiverName(address.getReceiverName());
        logistics.setReceiverPhone(address.getReceiverPhone());
        logistics.setReceiverAddress(address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress());

        orderLogisticsMapper.insert(logistics);

        // ========== 步骤 7：批量扣减 SKU 库存 ==========
        for (SkuStockInfo info : skuStockInfoList) {
            int updated = productSkuMapper.update(null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ProductSku>()
                            .eq(ProductSku::getId, info.sku.getId())
                            .setSql("stock = stock - " + info.quantity));
            if (updated <= 0) {
                throw new RuntimeException("扣减库存失败: " + info.sku.getId());
            }
        }

        // ========== 步骤 8：清理已下单的购物车项 ==========
        if (dto.getCartItemIds() != null && !dto.getCartItemIds().isEmpty()) {
            cartItemMapper.delete(new LambdaQueryWrapper<CartItem>()
                    .eq(CartItem::getUserId, Long.valueOf(userId))
                    .in(CartItem::getId, dto.getCartItemIds()));
        }

        // ========== 步骤 9：返回结果 ==========
        OrderCreateVO vo = new OrderCreateVO();
        vo.setOrderId(orderId);
        vo.setPayAmount(totalAmount);
        vo.setOrderStatus(1);
        return vo;
    }

    /**
     * 生成全局唯一订单号
     * 规则: ORD + yyyyMMddHHmmss + 4位随机数
     */
    private String generateOrderId() {
        String datePart = LocalDateTime.now().format(ORDER_ID_DATE_FMT);
        int random = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "ORD" + datePart + random;
    }

    /**
     * SKU 库存信息内部类
     */
    private record SkuStockInfo(ProductSku sku, ProductBase product, int quantity) {
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelOrder(String userId, OrderCancelDTO dto) {
        // ========== 步骤 1：身份 & 订单权限校验 ==========
        OrderInfo orderInfo = this.getById(dto.getOrderId());
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        if (orderInfo.getIsDeleted() != null && orderInfo.getIsDeleted() != 0) {
            throw new RuntimeException("订单已被删除");
        }
        if (orderInfo.getOrderStatus() != 1) {
            throw new RuntimeException("只能取消待付款状态的订单");
        }

        // ========== 步骤 2：查询订单商品明细，用于归还库存 ==========
        List<OrderItem> orderItems = orderItemMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, dto.getOrderId()));

        if (orderItems == null || orderItems.isEmpty()) {
            throw new RuntimeException("订单明细不存在");
        }

        // ========== 步骤 3：更新订单状态为已取消 ==========
        orderInfo.setOrderStatus(5); // 5-交易取消（已取消）
        orderInfo.setUpdateTime(LocalDateTime.now());
        this.updateById(orderInfo);

        // ========== 步骤 4：批量归还 SKU 库存 ==========
        for (OrderItem item : orderItems) {
            // 通过 productId + skuInfo 反查 SKU 记录
            ProductSku sku = productSkuMapper.selectOne(
                    new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<ProductSku>()
                            .eq(ProductSku::getProductId, item.getProductId())
                            .eq(ProductSku::getPropertyValueIdHash, item.getSkuInfo()));

            if (sku == null) {
                log.warn("取消订单归还库存时找不到SKU: productId={}, skuInfo={}, 跳过归还",
                        item.getProductId(), item.getSkuInfo());
                continue;
            }

            int updated = productSkuMapper.update(null,
                    new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<ProductSku>()
                            .eq(ProductSku::getId, sku.getId())
                            .setSql("stock = stock + " + item.getQuantity()));
            if (updated <= 0) {
                throw new RuntimeException("归还库存失败: " + sku.getId());
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmReceive(String userId, OrderConfirmReceiveDTO dto) {
        // ========== 步骤 1：权限与状态校验 ==========
        OrderInfo orderInfo = this.getById(dto.getOrderId());
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        if (orderInfo.getIsDeleted() != null && orderInfo.getIsDeleted() != 0) {
            throw new RuntimeException("订单已被删除");
        }
        if (orderInfo.getOrderStatus() != 3) {
            throw new RuntimeException("只能确认待收货状态的订单");
        }

        // ========== 步骤 2：更新订单状态为已完成 ==========
        orderInfo.setOrderStatus(4); // 4-已完成
        orderInfo.setUpdateTime(LocalDateTime.now());
        this.updateById(orderInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(String userId, OrderSoftDeleteDTO dto) {
        // ========== 步骤 1：权限与状态校验 ==========
        OrderInfo orderInfo = this.getById(dto.getOrderId());
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        if (!orderInfo.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作该订单");
        }
        if (orderInfo.getIsDeleted() != null && orderInfo.getIsDeleted() != 0) {
            throw new RuntimeException("订单已被删除");
        }

        // ========== 步骤 2：软删除订单 ==========
        orderInfo.setIsDeleted(1); // 1-已软删除
        orderInfo.setUpdateTime(LocalDateTime.now());
        this.updateById(orderInfo);
    }

    @Override
    public IPage<OrderItemVO> getOrderItemsByUserId(String userId, long pageNum, long pageSize) {
        // 入参校验
        if (StrUtil.isBlank(userId)) {
            return new Page<>(pageNum, pageSize);
        }

        // 1. 先查出该用户所有未删除的订单ID及状态
        List<OrderInfo> orderList = this.list(
                Wrappers.lambdaQuery(OrderInfo.class)
                        .eq(OrderInfo::getUserId, userId)
                        .eq(OrderInfo::getIsDeleted, 0)
                        .select(OrderInfo::getOrderId, OrderInfo::getOrderStatus)
        );

        if (orderList.isEmpty()) {
            return new Page<>(pageNum, pageSize);
        }

        // 构建 orderId -> orderStatus 映射
        java.util.Map<String, Integer> orderStatusMap = orderList.stream()
                .collect(java.util.stream.Collectors.toMap(OrderInfo::getOrderId, OrderInfo::getOrderStatus));

        List<String> orderIds = orderList.stream()
                .map(OrderInfo::getOrderId)
                .toList();

        // 2. 直接对 order_item 表做分页查询，按创建时间倒序
        IPage<OrderItem> itemPage = orderItemMapper.selectPage(
                new Page<>(pageNum, pageSize),
                Wrappers.lambdaQuery(OrderItem.class)
                        .in(OrderItem::getOrderId, orderIds)
                        .orderByDesc(OrderItem::getCreateTime)
        );

        if (itemPage.getRecords().isEmpty()) {
            return new Page<>(pageNum, pageSize, itemPage.getTotal());
        }

        // 3. 转换为 VO，填充订单状态
        List<OrderItemVO> voList = new ArrayList<>();
        for (OrderItem item : itemPage.getRecords()) {
            OrderItemVO vo = new OrderItemVO();
            BeanUtil.copyProperties(item, vo);
            vo.setOrderStatus(orderStatusMap.get(item.getOrderId()));
            voList.add(vo);
        }

        // 4. 构造分页结果
        IPage<OrderItemVO> resultPage = new Page<>(pageNum, pageSize);
        resultPage.setRecords(voList);
        resultPage.setTotal(itemPage.getTotal());
        resultPage.setPages(itemPage.getPages());
        return resultPage;
    }
}
