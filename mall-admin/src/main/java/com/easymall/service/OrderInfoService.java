package com.easymall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.DTO.OrderDeliveryDTO;
import com.easymall.entity.Order.OrderInfo;
import com.easymall.entity.OrderInfoQuery;
import com.easymall.entity.vo.OrderInfoVO;

/**
 * 订单服务接口
 */
public interface OrderInfoService extends IService<OrderInfo> {
    
    /**
     * 分页查询订单列表（返回VO）
     * @param query 查询条件
     * @return 订单分页数据
     */
    IPage<OrderInfoVO> getOrderPageVO(OrderInfoQuery query);
    
    /**
     * 订单发货
     * @param deliveryDTO 发货信息
     */
    void delivery(OrderDeliveryDTO deliveryDTO);
    
    /**
     * 分页查询订单列表
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param orderStatus 订单状态（可选）
     * @param prodName 买家信息（可选）
     * @return 订单分页数据
     */
    IPage<OrderInfo> getOrderPage(Integer pageNum, Integer pageSize, Integer orderStatus, String prodName);
}
