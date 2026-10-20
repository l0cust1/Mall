package com.easymall.service;

import com.easymall.entity.UserAddress;
import com.easymall.entity.VO.UserAddressVO;

import java.util.List;

public interface UserAddressService {

    /**
     * 新增收货地址
     */
    void addAddress(UserAddress userAddress);

    /**
     * 根据地址ID删除地址
     */
    void deleteById(Long addressId);

    /**
     * 修改收货地址
     */
    void updateAddress(UserAddress userAddress);

    /**
     * 根据地址ID查询地址详情
     */
    UserAddress getById(Long addressId);

    /**
     * 查询用户所有地址列表（默认地址优先，再按创建时间倒序）
     */
    List<UserAddress> listByUserId(Long userId);

    /**
     * 查询用户所有地址列表（返回VO）
     */
    List<UserAddressVO> listAddressVOByUserId(String userId);
}
