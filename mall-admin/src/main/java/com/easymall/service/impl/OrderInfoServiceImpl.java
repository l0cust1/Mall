package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.DTO.OrderDeliveryDTO;
import com.easymall.entity.Order.OrderInfo;
import com.easymall.entity.Order.OrderItem;
import com.easymall.entity.Order.OrderLogistics;
import com.easymall.entity.OrderInfoQuery;
import com.easymall.entity.vo.OrderInfoVO;
import com.easymall.mapper.OrderInfoMapper;
import com.easymall.service.OrderInfoService;
import com.easymall.service.OrderItemService;
import com.easymall.service.OrderLogisticsService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private OrderLogisticsService orderLogisticsService;

    /**
     * 分页查询订单列表（返回VO）
     */
    @Override
    public IPage<OrderInfoVO> getOrderPageVO(OrderInfoQuery query) {
        // 1. 构建分页对象
        Page<OrderInfo> page = new Page<>(query.getPageNum(), query.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的订单（is_deleted = 0）
        wrapper.eq(OrderInfo::getIsDeleted, 0);
        
        // 订单ID精确查询
        if (StringUtils.hasText(query.getOrderId())) {
            wrapper.eq(OrderInfo::getOrderId, query.getOrderId());
        }
        
        // 商品名称模糊查询（需要关联order_item表）
        if (StringUtils.hasText(query.getProductName())) {
            // 先查询包含该商品名称的订单ID
            List<OrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>()
                    .like(OrderItem::getProductName, query.getProductName())
                    .select(OrderItem::getOrderId)
            );
            if (items != null && !items.isEmpty()) {
                List<String> orderIds = items.stream()
                    .map(OrderItem::getOrderId)
                    .distinct()
                    .collect(Collectors.toList());
                wrapper.in(OrderInfo::getOrderId, orderIds);
            } else {
                // 如果没有匹配的商品，返回空结果
                wrapper.eq(OrderInfo::getOrderId, "");
            }
        }
        
        // 订单状态筛选
        if (query.getOrderStatus() != null) {
            wrapper.eq(OrderInfo::getOrderStatus, query.getOrderStatus());
        }
        
        // 按创建时间降序
        wrapper.orderByDesc(OrderInfo::getCreateTime);

        // 3. 执行分页查询
        IPage<OrderInfo> orderPage = this.page(page, wrapper);

        // 4. 转换为VO
        IPage<OrderInfoVO> voPage = new Page<>(orderPage.getCurrent(), orderPage.getSize(), orderPage.getTotal());
        List<OrderInfoVO> voList = orderPage.getRecords().stream().map(order -> {
            OrderInfoVO vo = new OrderInfoVO();
            BeanUtils.copyProperties(order, vo);
            
            // 设置订单状态文本
            vo.setOrderStatusText(getOrderStatusText(order.getOrderStatus()));
            
            // 查询订单商品明细
            List<OrderItem> items = orderItemService.list(
                new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getOrderId())
            );
            
            // 转换为VO的item列表
            List<OrderInfoVO.OrderItemVO> itemVOList = items.stream().map(item -> {
                OrderInfoVO.OrderItemVO itemVO = new OrderInfoVO.OrderItemVO();
                BeanUtils.copyProperties(item, itemVO);
                return itemVO;
            }).collect(Collectors.toList());
            
            vo.setItems(itemVOList);
            
            // 计算总数量
            int totalCount = items.stream().mapToInt(OrderItem::getQuantity).sum();
            vo.setTotalCount(totalCount);
            
            return vo;
        }).collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 订单发货
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delivery(OrderDeliveryDTO deliveryDTO) {
        // 1. 查询订单
        OrderInfo orderInfo = this.getById(deliveryDTO.getOrderId());
        if (orderInfo == null) {
            throw new RuntimeException("订单不存在");
        }
        
        // 2. 验证订单状态（只有已付款待发货的订单才能发货）
        if (orderInfo.getOrderStatus() != 2) {
            throw new RuntimeException("订单状态不是待发货，无法进行发货操作");
        }
        
        // 3. 更新订单状态为已发货
        orderInfo.setOrderStatus(3);
        orderInfo.setUpdateTime(LocalDateTime.now());
        this.updateById(orderInfo);
        
        // 4. 保存或更新物流信息
        OrderLogistics existLogistics = orderLogisticsService.getLogisticsByOrderId(deliveryDTO.getOrderId());
        
        if (existLogistics != null) {
            // 如果已存在物流记录，则更新
            existLogistics.setExpressCompany(deliveryDTO.getExpressCompany());
            existLogistics.setExpressNo(deliveryDTO.getExpressNo());
            existLogistics.setDeliveryTime(LocalDateTime.now());
            existLogistics.setReceiverName(deliveryDTO.getReceiverName());
            existLogistics.setReceiverPhone(deliveryDTO.getReceiverPhone());
            existLogistics.setReceiverAddress(deliveryDTO.getReceiverAddress());
            existLogistics.setUpdateTime(LocalDateTime.now());
            orderLogisticsService.updateById(existLogistics);
        } else {
            // 如果不存在，则新增
            OrderLogistics logistics = new OrderLogistics();
            logistics.setOrderId(deliveryDTO.getOrderId());
            logistics.setExpressCompany(deliveryDTO.getExpressCompany());
            logistics.setExpressNo(deliveryDTO.getExpressNo());
            logistics.setDeliveryTime(LocalDateTime.now());
            logistics.setReceiverName(deliveryDTO.getReceiverName());
            logistics.setReceiverPhone(deliveryDTO.getReceiverPhone());
            logistics.setReceiverAddress(deliveryDTO.getReceiverAddress());
            logistics.setCreateTime(LocalDateTime.now());
            logistics.setUpdateTime(LocalDateTime.now());
            orderLogisticsService.save(logistics);
        }
    }

    /**
     * 分页查询订单列表
     */
    @Override
    public IPage<OrderInfo> getOrderPage(Integer pageNum, Integer pageSize, Integer orderStatus, String prodName) {
        Page<OrderInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();

        wrapper.ne(OrderInfo::getIsDeleted, 0);

        if (orderStatus != null) {
            wrapper.eq(OrderInfo::getOrderStatus, orderStatus);
        }

        wrapper.orderByDesc(OrderInfo::getCreateTime);

        return this.page(page, wrapper);
    }

    /**
     * 获取订单状态文本
     */
    private String getOrderStatusText(Integer orderStatus) {
        if (orderStatus == null) {
            return "未知";
        }
        switch (orderStatus) {
            case 0:
                return "已删除";
            case 1:
                return "待付款";
            case 2:
                return "已付款,待发货";
            case 3:
                return "已发货";
            case 4:
                return "已完成";
            case 5:
                return "交易取消";
            case 6:
                return "交易关闭";
            case 7:
                return "已退款,交易关闭";
            default:
                return "未知";
        }
    }

    /**
     * 根据订单ID查询订单详情
     */


}
