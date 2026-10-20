package com.easymall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.DTO.OrderCancelDTO;
import com.easymall.entity.DTO.OrderConfirmReceiveDTO;
import com.easymall.entity.DTO.OrderCreateDTO;
import com.easymall.entity.DTO.OrderSoftDeleteDTO;
import com.easymall.entity.Order.OrderInfo;
import com.easymall.entity.VO.OrderCreateVO;
import com.easymall.entity.VO.OrderItemVO;

/**
 * 订单服务接口
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 创建订单
     * @param userId 当前登录用户 ID
     * @param dto 订单创建请求
     * @return 订单创建结果
     */
    OrderCreateVO createOrder(String userId, OrderCreateDTO dto);

    /**
     * 取消订单
     * @param userId 当前登录用户 ID
     * @param dto 取消订单请求
     */
    void cancelOrder(String userId, OrderCancelDTO dto);

    /**
     * 确认收货
     * @param userId 当前登录用户 ID
     * @param dto 确认收货请求
     */
    void confirmReceive(String userId, OrderConfirmReceiveDTO dto);

    /**
     * 软删除订单
     * @param userId 当前登录用户 ID
     * @param dto 软删除请求
     */
    void deleteOrder(String userId, OrderSoftDeleteDTO dto);

    /**
     * 根据用户ID分页获取订单商品明细
     * @param userId 当前登录用户 ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 订单商品明细分页数据
     */
    IPage<OrderItemVO> getOrderItemsByUserId(String userId, long pageNum, long pageSize);
}
