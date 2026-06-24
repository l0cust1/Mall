package com.easymall.controller;

import com.easymall.entity.SysUser;
import com.easymall.entity.SysUserQuery;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.SysUserService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sysUser")
public class SysUserController extends ABaseController {

    @Resource
    private SysUserService sysUserService;

    /**
     * 分页查询用户列表
     */
    @RequestMapping("/loadUserList")
    public ResponseVO loadUserList(SysUserQuery query) {
        return getSuccessResponseVO(sysUserService.findListByParam(query));
    }

    /**
     * 禁用/启用用户
     * @param userId 用户ID
     * @param status 状态：0-禁用，1-正常
     */
    @RequestMapping("/updateUserStatus")
    public ResponseVO updateUserStatus(String userId, Integer status) {
        sysUserService.updateUserStatus(userId, status);
        return getSuccessResponseVO(null);
    }
}
