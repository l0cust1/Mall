package com.easymall.controller;

import com.easymall.entity.DTO.*;
import com.easymall.entity.SysUser;
import com.easymall.entity.VO.UserAddressVO;
import com.easymall.entity.VO.UserInfoVO;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.SysUserService;
import com.easymall.service.UserAddressService;
import com.easymall.service.VerificationCodeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/info")
public class UserInfoController extends ABaseController{

    @Autowired
    private SysUserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private UserAddressService userAddressService;

    // 获取当前登录用户（从请求域或通过token解析）
    private SysUser getCurrentUser(HttpServletRequest request) {
        // 假设在JWT拦截器中已将用户存入request attribute
        return (SysUser) request.getAttribute("currentUser");
    }
    /**
     * 获取用户当前个人信息
     */
    @GetMapping("/myinfo")
    public ResponseVO<UserInfoVO> getMyInfo(HttpServletRequest request) {
        SysUser user = getCurrentUser(request);
        if (user == null) {
            return getFailResponseVO("用户未登录");
        }

        UserInfoVO vo = new UserInfoVO();
        BeanUtils.copyProperties(user, vo);
        return getSuccessResponseVO(vo);
    }

    /**
     * 获取当前用户的收货地址列表
     */
    @GetMapping("/addressList")
    public ResponseVO<List<UserAddressVO>> getAddressList(HttpServletRequest request) {
        SysUser user = getCurrentUser(request);
        if (user == null) {
            return getFailResponseVO("用户未登录");
        }
        List<UserAddressVO> addressList = userAddressService.listAddressVOByUserId(user.getUserId());
        return getSuccessResponseVO(addressList);
    }

    /**
     * 修改性别
     */
    @PostMapping("/gender")
    public ResponseVO<String> updateGender(@RequestBody GenderUpdateRequest request, HttpServletRequest req) {
        SysUser user = getCurrentUser(req);
        if (user == null) {
            return getFailResponseVO("用户未登录");
        }
        if (request.getGender() == null || (request.getGender() != 1 && request.getGender() != 2)) {
            return getFailResponseVO("性别参数错误，请传1-男或2-女");
        }
        user.setGender(request.getGender());
        userService.updateById(user);
        return getSuccessResponseVO("性别修改成功");
    }

    /**
     * 修改头像
     */
    @PostMapping("/avatar")
    public ResponseVO<String> updateAvatar(@RequestBody AvatarUpdateRequest request, HttpServletRequest req) {
        SysUser user = getCurrentUser(req);
        if (user == null) {
            return getFailResponseVO("用户未登录");
        }
        if (request.getAvatar() == null || request.getAvatar().trim().isEmpty()) {
            return getFailResponseVO("头像URL不能为空");
        }
        user.setAvatar(request.getAvatar());
        userService.updateById(user);
        return getSuccessResponseVO("头像修改成功");
    }

    /**
     * 修改密码
     */
    @PostMapping("/password")
    public ResponseVO<String> updatePassword(@RequestBody PasswordUpdateRequest request, HttpServletRequest req) {
        SysUser user = getCurrentUser(req);
        if (user == null) {
            return getFailResponseVO("用户未登录");
        }
        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return getFailResponseVO("原密码错误");
        }
        // 新密码加密
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userService.updateById(user);
        return getSuccessResponseVO("密码修改成功");
    }

    /**
     * 绑定手机号（需验证码）
     */
    @PostMapping("/phone")
    public ResponseVO<String> bindPhone(@RequestBody BindPhoneRequest request, HttpServletRequest req) {
        SysUser user = getCurrentUser(req);
        if (user == null) {
            return getFailResponseVO("用户未登录");
        }
        // 校验验证码
        boolean valid = verificationCodeService.validateCode(request.getPhone(), request.getCode());
        if (!valid) {
            return getFailResponseVO("验证码错误或已过期");
        }
        // 检查手机号是否已被其他用户绑定
        SysUser existUser = userService.findByPhone(request.getPhone());
        if (existUser != null && !existUser.getUserId().equals(user.getUserId())) {
            return getFailResponseVO("该手机号已被其他用户绑定");
        }
        user.setPhone(request.getPhone());
        userService.updateById(user);
        // 清除验证码
        verificationCodeService.deleteCode(request.getPhone());
        return getSuccessResponseVO("手机绑定成功");
    }

    /**
     * 绑定邮箱（需验证码）
     */
    @PostMapping("/email")
    public ResponseVO<String> bindEmail(@RequestBody BindEmailRequest request, HttpServletRequest req) {
        SysUser user = getCurrentUser(req);
        if (user == null) {
            return getFailResponseVO("用户未登录");
        }
        // 校验邮箱验证码（需单独发送邮箱验证码，此处用 phone 验证码逻辑复用，但实际应区分，为示例沿用）
        // 假设 verificationCodeService 支持邮箱，这里简化为复用，实际可增加 email 验证码服务
        boolean valid = verificationCodeService.validateCode(request.getEmail(), request.getCode());
        if (!valid) {
            return getFailResponseVO("验证码错误或已过期");
        }
        // 检查邮箱是否已被其他用户绑定
        SysUser existUser = userService.findByEmail(request.getEmail());
        if (existUser != null && !existUser.getUserId().equals(user.getUserId())) {
            return getFailResponseVO("该邮箱已被其他用户绑定");
        }
        user.setEmail(request.getEmail());
        userService.updateById(user);
        verificationCodeService.deleteCode(request.getEmail());
        return getSuccessResponseVO("邮箱绑定成功");
    }

}
