package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.SysUser;
import com.easymall.entity.SysUserQuery;
import com.easymall.mapper.SysUserMapper;
import com.easymall.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public IPage<SysUser> findListByParam(SysUserQuery query) {
        // 1. 构建分页对象
        Page<SysUser> page = new Page<>(query.getPageNum(), query.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 昵称 模糊查询
        if (query.getNickname() != null && !query.getNickname().isEmpty()) {
            wrapper.like(SysUser::getNickname, query.getNickname());
        }

        // 状态 精确查询
        if (query.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, query.getStatus());
        }

        // 按创建时间倒序
        wrapper.orderByDesc(SysUser::getCreateTime);

        // 3. 查询并返回
        return sysUserMapper.selectPage(page, wrapper);
    }

    @Override
    public void updateUserStatus(String userId, Integer status) {
        // 验证用户是否存在
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新状态
        SysUser updateUser = new SysUser();
        updateUser.setUserId(userId);
        updateUser.setStatus(status);
        sysUserMapper.updateById(updateUser);
    }
}
