package com.easymall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easymall.entity.DTO.OrderCancelDTO;
import com.easymall.entity.DTO.OrderConfirmReceiveDTO;
import com.easymall.entity.DTO.OrderCreateDTO;
import com.easymall.entity.DTO.OrderSoftDeleteDTO;
import com.easymall.entity.SysUser;
import com.easymall.entity.VO.OrderConfirmVO;
import com.easymall.entity.VO.OrderCreateVO;
import com.easymall.entity.VO.OrderItemVO;
import com.easymall.result.ApiResult;
import com.easymall.service.OrderInfoService;
import com.easymall.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 订单控制器（前端用户侧）
 */
@Tag(name = "订单模块", description = "订单确认、创建等前端用户接口")
@RestController
@RequestMapping("/order")
@Slf4j
public class OrderController {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderInfoService orderInfoService;

    /**
     * 获取订单确认信息
     * GET /order/getConfirmGoods/{skuId}
     */
    @Operation(summary = "获取订单确认信息", description = "根据SKU ID获取商品确认订单信息，包括商品名称、封面、规格、价格等。")
    @GetMapping("/getConfirmGoods/{property_value_id_hash}")
    public ApiResult<OrderConfirmVO> getConfirmGoods(@PathVariable String property_value_id_hash) {
        log.info("获取订单确认信息, skuId: {}", property_value_id_hash);

        try {
            OrderConfirmVO vo = productService.getOrderConfirmInfo(property_value_id_hash);
            return ApiResult.success(vo);
        } catch (RuntimeException e) {
            log.error("订单确认失败, skuId: {}", property_value_id_hash, e);
            return ApiResult.error(400, e.getMessage());
        }
    }


    /**
     * 获取用户所有订单商品明细（分页）
     * GET /order/showOrder
     */
    @Operation(summary = "获取用户所有订单商品明细", description = "分页获取当前登录用户所有订单的商品明细信息，按时间倒序。")
    @GetMapping("/showOrder")
    public ApiResult<IPage<OrderItemVO>> getOrderItems(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            HttpServletRequest request) {
        log.info("获取订单商品明细, userId: {}, pageNum: {}, pageSize: {}", getUserId(request), pageNum, pageSize);

        try {
            String userId = getUserId(request);
            IPage<OrderItemVO> page = orderInfoService.getOrderItemsByUserId(userId, pageNum, pageSize);
            return ApiResult.success(page);
        } catch (RuntimeException e) {
            log.error("获取订单商品明细失败, userId: {}", getUserId(request), e);
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("获取订单商品明细异常, userId: {}", getUserId(request), e);
            return ApiResult.error(500, "获取订单商品明细失败，请稍后重试");
        }
    }

    /**
     * 创建订单
     * POST /order/create
     */
    @Operation(summary = "创建订单", description = "用户立即购买/购物车结算时生成订单，事务中同时插入订单主表和明细表。")
    @PostMapping("/create")
    public ApiResult<OrderCreateVO> createOrder(@Valid @RequestBody OrderCreateDTO dto,
                                                 HttpServletRequest request) {
        log.info("创建订单，userId: {}, addressId: {}, itemCount: {}",
                getUserId(request), dto.getAddressId(),
                dto.getOrderItemList() != null ? dto.getOrderItemList().size() : 0);

        try {
            String userId = getUserId(request);
            OrderCreateVO vo = orderInfoService.createOrder(userId, dto);
            return ApiResult.success(vo);
        } catch (RuntimeException e) {
            log.error("创建订单失败", e);
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("创建订单异常", e);
            return ApiResult.error(500, "订单创建失败，请稍后重试");
        }
    }

    /**
     * 取消订单
     * PUT /order/cancel
     */
    @Operation(summary = "取消订单", description = "用户取消待付款订单，事务中更新订单状态并归还商品库存。")
    @PutMapping("/cancel")
    public ApiResult<Void> cancelOrder(@Valid @RequestBody OrderCancelDTO dto,
                                        HttpServletRequest request) {
        log.info("取消订单，userId: {}, orderId: {}", getUserId(request), dto.getOrderId());

        try {
            String userId = getUserId(request);
            orderInfoService.cancelOrder(userId, dto);
            return ApiResult.success(null, "订单取消成功，已释放商品库存");
        } catch (RuntimeException e) {
            log.error("取消订单失败，orderId: {}", dto.getOrderId(), e);
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("取消订单异常，orderId: {}", dto.getOrderId(), e);
            return ApiResult.error(500, "订单取消失败，请稍后重试");
        }
    }

    /**
     * 确认收货
     * PUT /order/confirmReceive
     */
    @Operation(summary = "确认收货", description = "用户确认已收到商品，将订单状态从待收货修改为已完成。")
    @PutMapping("/confirmReceive")
    public ApiResult<Void> confirmReceive(@Valid @RequestBody OrderConfirmReceiveDTO dto,
                                           HttpServletRequest request) {
        log.info("确认收货，userId: {}, orderId: {}", getUserId(request), dto.getOrderId());

        try {
            String userId = getUserId(request);
            orderInfoService.confirmReceive(userId, dto);
            return ApiResult.success(null, "确认收货成功，订单已完成，可前往评价商品");
        } catch (RuntimeException e) {
            log.error("确认收货失败，orderId: {}", dto.getOrderId(), e);
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("确认收货异常，orderId: {}", dto.getOrderId(), e);
            return ApiResult.error(500, "确认收货失败，请稍后重试");
        }
    }

    /**
     * 软删除订单
     * PUT /order/delete
     */
    @Operation(summary = "删除订单", description = "用户端隐藏订单（软删除），不影响订单明细数据。")
    @PutMapping("/delete")
    public ApiResult<Void> deleteOrder(@Valid @RequestBody OrderSoftDeleteDTO dto,
                                        HttpServletRequest request) {
        log.info("软删除订单，userId: {}, orderId: {}", getUserId(request), dto.getOrderId());

        try {
            String userId = getUserId(request);
            orderInfoService.deleteOrder(userId, dto);
            return ApiResult.success(null, "订单已删除");
        } catch (RuntimeException e) {
            log.error("软删除订单失败，orderId: {}", dto.getOrderId(), e);
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("软删除订单异常，orderId: {}", dto.getOrderId(), e);
            return ApiResult.error(500, "删除订单失败，请稍后重试");
        }
    }

    private String getUserId(HttpServletRequest request) {
        SysUser user = (SysUser) request.getAttribute("currentUser");
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }
        return user.getUserId().toString();
    }
}
