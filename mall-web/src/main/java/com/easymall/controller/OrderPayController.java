package com.easymall.controller;

import com.easymall.entity.DTO.PayCreateDTO;
import com.easymall.entity.SysUser;
import com.easymall.entity.VO.PayCreateVO;
import com.easymall.entity.VO.PayQueryVO;
import com.easymall.result.ApiResult;
import com.easymall.service.OrderPaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单支付控制器（前端用户侧）
 */
@Tag(name = "订单支付模块", description = "订单支付相关接口")
@RestController
@RequestMapping("/pay")
@Slf4j
public class OrderPayController {

    @Autowired
    private OrderPaymentService orderPaymentService;

    /**
     * 发起支付
     * POST /pay/create
     */
    @Operation(summary = "发起支付", description = "用户对待付款订单发起支付，生成支付流水并返回第三方支付链接。")
    @PostMapping("/create")
    public ApiResult<PayCreateVO> createPay(@Valid @RequestBody PayCreateDTO dto,
                                             HttpServletRequest request) {
        log.info("发起支付，userId: {}, orderId: {}, paymentMethod: {}",
                getUserId(request), dto.getOrderId(), dto.getPaymentMethod());

        try {
            String userId = getUserId(request);
            PayCreateVO vo = orderPaymentService.createPay(userId, dto);
            return ApiResult.success(vo, "支付单创建成功，请前往付款");
        } catch (RuntimeException e) {
            log.error("发起支付失败，orderId: {}", dto.getOrderId(), e);
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("发起支付异常，orderId: {}", dto.getOrderId(), e);
            return ApiResult.error(500, "支付失败，请稍后重试");
        }
    }

    /**
     * 查询支付状态
     * GET /pay/query
     */
    @Operation(summary = "查询支付状态", description = "前端轮询查询支付状态，直到支付成功或失败。")
    @GetMapping("/query")
    public ApiResult<PayQueryVO> queryPayStatus(@RequestParam("payment_no") String paymentNo,
                                                 HttpServletRequest request) {
        log.info("查询支付状态，userId: {}, paymentNo: {}", getUserId(request), paymentNo);

        try {
            String userId = getUserId(request);
            PayQueryVO vo = orderPaymentService.queryPayStatus(userId, paymentNo);
            return ApiResult.success(vo, "查询成功");
        } catch (RuntimeException e) {
            log.error("查询支付状态失败，paymentNo: {}", paymentNo, e);
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询支付状态异常，paymentNo: {}", paymentNo, e);
            return ApiResult.error(500, "查询失败，请稍后重试");
        }
    }

    private String getUserId(HttpServletRequest request) {
        SysUser user = (SysUser) request.getAttribute("currentUser");
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }
        return user.getUserId().toString();
    }

    /**
     * 支付宝异步支付回调接口
     * POST /pay/callback
     * 支付宝服务器主动调用，无登录 Token
     * 返回支付宝标准响应：success 或 fail
     */
    @Operation(summary = "支付宝异步回调", description = "支付宝服务器主动调用的异步通知接口，返回 success 或 fail")
    @PostMapping(value = "/callback", produces = MediaType.TEXT_PLAIN_VALUE)
    public String alipayCallback(HttpServletRequest request) {
        log.info("========== 收到支付宝异步回调请求 ==========");

        // 接收支付宝全部回调参数
        Map<String, String[]> rawParams = request.getParameterMap();
        Map<String, String> params = new HashMap<>();
        for (Map.Entry<String, String[]> entry : rawParams.entrySet()) {
            String[] values = entry.getValue();
            params.put(entry.getKey(), values.length > 0 ? values[0] : "");
        }

        log.info("支付宝回调原始参数: {}", params);

        try {
            String result = orderPaymentService.handlePayCallback(params);
            log.info("支付宝回调处理完成，返回: {}", result);
            return result;
        } catch (Exception e) {
            log.error("支付宝回调处理异常", e);
            return "fail";
        }
    }
}
