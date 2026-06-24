package com.easymall.controller;

import com.easymall.entity.ShippingInfo;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.ShippingInfoService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shipping")
public class ShippingInfoController extends ABaseController {

    @Resource
    private ShippingInfoService shippingInfoService;

    /**
     * 获取发货信息
     */
    @RequestMapping("/getShippingInfo")
    public ResponseVO getShippingInfo() {
        return getSuccessResponseVO(shippingInfoService.getShippingInfo());
    }

    /**
     * 保存发货信息
     */
    @RequestMapping("/saveShippingInfo")
    public ResponseVO saveShippingInfo(@RequestBody ShippingInfo shippingInfo) {
        shippingInfoService.saveShippingInfo(shippingInfo);
        return getSuccessResponseVO(null);
    }
}
