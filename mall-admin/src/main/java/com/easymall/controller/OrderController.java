package com.easymall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easymall.entity.DTO.OrderDeliveryDTO;
import com.easymall.entity.OrderInfoQuery;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.entity.vo.OrderInfoVO;
import com.easymall.service.OrderInfoService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 订单管理Controller
 */
@RestController
@RequestMapping("/order")
public class OrderController extends ABaseController {

    @Resource
    private OrderInfoService orderInfoService;

    /**
     * 分页查询订单列表
     * @param query 查询条件
     * @return 订单分页数据
     */
    @GetMapping("/list")
    public ResponseVO getOrderList(OrderInfoQuery query) {
        IPage<OrderInfoVO> page = orderInfoService.getOrderPageVO(query);
        return getSuccessResponseVO(page);
    }

    /**
     * 订单发货
     * @param deliveryDTO 发货信息
     * @return 操作结果
     */
    @PostMapping("/delivery")
    public ResponseVO delivery(@Valid @RequestBody OrderDeliveryDTO deliveryDTO) {
        try {
            orderInfoService.delivery(deliveryDTO);
            return getSuccessResponseVO("发货成功");
        } catch (Exception e) {
            return getFailResponseVO(e.getMessage());
        }
    }
}
