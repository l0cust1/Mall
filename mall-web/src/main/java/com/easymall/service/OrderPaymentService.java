package com.easymall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.DTO.PayCreateDTO;
import com.easymall.entity.Order.OrderPayment;
import com.easymall.entity.VO.PayCreateVO;
import com.easymall.entity.VO.PayQueryVO;

import java.util.Map;

/**
 * 订单支付服务接口
 */
public interface OrderPaymentService extends IService<OrderPayment> {

    /**
     * 发起支付
     * @param userId 当前登录用户 ID
     * @param dto 支付请求
     * @return 支付结果
     */
    PayCreateVO createPay(String userId, PayCreateDTO dto);

    /**
     * 处理支付宝异步回调
     * @param params 支付宝回调的全部表单参数
     * @return "success" 或 "fail"
     */
    String handlePayCallback(Map<String, String> params);

    /**
     * 查询支付状态
     * @param userId 当前登录用户 ID
     * @param paymentNo 支付流水号
     * @return 支付状态信息
     */
    PayQueryVO queryPayStatus(String userId, String paymentNo);
}
