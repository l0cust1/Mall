package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.UserAddress;
import com.easymall.entity.VO.UserAddressVO;
import com.easymall.mapper.UserAddressMapper;
import com.easymall.service.UserAddressService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements UserAddressService {

    @Override
    public void addAddress(UserAddress userAddress) {
        // 如果设置为默认地址，先取消该用户其他地址的默认标识
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault() == 1) {
            clearDefaultByUserId(userAddress.getUserId());
        }
        save(userAddress);
    }

    @Override
    public void deleteById(Long addressId) {
        removeById(addressId);
    }

    @Override
    public void updateAddress(UserAddress userAddress) {
        // 如果设置为默认地址，先取消该用户其他地址的默认标识
        if (userAddress.getIsDefault() != null && userAddress.getIsDefault() == 1) {
            clearDefaultByUserId(userAddress.getUserId());
        }
        updateById(userAddress);
    }

    @Override
    public UserAddress getById(Long addressId) {
        return baseMapper.selectById(addressId);
    }

    @Override
    public List<UserAddress> listByUserId(Long userId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime);
        return baseMapper.selectList(wrapper);
    }

    @Override
    public List<UserAddressVO> listAddressVOByUserId(String userId) {
        LambdaQueryWrapper<UserAddress> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserAddress::getUserId, Long.valueOf(userId))
                .orderByDesc(UserAddress::getIsDefault)
                .orderByDesc(UserAddress::getCreateTime);
        
        List<UserAddress> addressList = baseMapper.selectList(wrapper);
        return addressList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 取消指定用户所有地址的默认标识
     */
    private void clearDefaultByUserId(Long userId) {
        LambdaUpdateWrapper<UserAddress> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserAddress::getUserId, userId)
                .set(UserAddress::getIsDefault, 0);
        baseMapper.update(null, wrapper);
    }

    /**
     * 将UserAddress转换为UserAddressVO
     */
    private UserAddressVO convertToVO(UserAddress address) {
        UserAddressVO vo = new UserAddressVO();
        BeanUtils.copyProperties(address, vo);
        vo.setIsDefault(address.getIsDefault() != null && address.getIsDefault() == 1);
        return vo;
    }
}
