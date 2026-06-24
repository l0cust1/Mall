package com.easymall.service.impl;

import com.easymall.entity.ShippingInfo;
import com.easymall.entity.constants.Constants;
import com.easymall.service.ShippingInfoService;
import com.easymall.utils.RedisUtils;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import static com.easymall.entity.constants.Constants.REDIS_KEY_EXPIRES_DAY;

@Service
public class ShippingInfoServiceImpl implements ShippingInfoService {

    @Resource
    private RedisUtils redisUtils;

    @Override
    public ShippingInfo getShippingInfo() {
        // 从 Redis 中获取发货信息
        Object obj = redisUtils.get(Constants.REDIS_KEY_SHIPPING_INFO);
        if (obj != null) {
            return (ShippingInfo) obj;
        }
        // 如果 Redis 中没有数据，返回默认值
        return getDefaultShippingInfo();
    }

    @Override
    public void saveShippingInfo(ShippingInfo shippingInfo) {
        // 保存到 Redis，不设置过期时间（永久保存）
        redisUtils.set(Constants.REDIS_KEY_SHIPPING_INFO, shippingInfo,REDIS_KEY_EXPIRES_DAY);
    }

    /**
     * 获取默认发货信息
     */
    private ShippingInfo getDefaultShippingInfo() {
        ShippingInfo defaultInfo = new ShippingInfo();
        defaultInfo.setSenderName("默认发货人");
        defaultInfo.setSenderPhone("13800138000");
        defaultInfo.setSenderAddress("默认发货地址");
        defaultInfo.setDefaultExpress("顺丰快递");
        defaultInfo.setRemark("");
        return defaultInfo;
    }
}
