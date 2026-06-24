package com.easymall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.SysUser;
import com.easymall.entity.SysUserQuery;

public interface SysUserService extends IService<SysUser> {
    /**
     * 分页查询用户列表
     */
    IPage<SysUser> findListByParam(SysUserQuery query);
    
    /**
     * 禁用/启用用户
     */
    void updateUserStatus(String userId, Integer status);

    SysUser findByUsername(String username);

    SysUser findByPhone(String phone);

    SysUser findByEmail(String email);
}
