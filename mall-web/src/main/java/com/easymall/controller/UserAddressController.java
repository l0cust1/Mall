package com.easymall.controller;

import com.easymall.entity.SysUser;
import com.easymall.entity.UserAddress;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.UserAddressService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户收货地址控制器
 */
@RestController
@RequestMapping("/address")
@Slf4j
@Validated
public class UserAddressController extends ABaseController {

    @Autowired
    private UserAddressService userAddressService;
    private String getUserId(HttpServletRequest request) {
        SysUser user = (SysUser) request.getAttribute("currentUser");
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }
        return user.getUserId().toString();
    }
    /**
     * 新增收货地址
     * POST /address/add
     */
    @PostMapping("/add")
    public ResponseVO<String> addAddress(@Valid @RequestBody UserAddress userAddress,HttpServletRequest request) {
        try {
            String userId = getUserId(request);
            userAddress.setUserId(Long.valueOf(userId));
            userAddressService.addAddress(userAddress);
            return getSuccessResponseVO("新增成功");
        } catch (Exception e) {
            log.error("新增地址失败: {}", e.getMessage(), e);
            return getFailResponseVO("新增地址失败: " + e.getMessage());
        }
    }

    /**
     * 根据地址ID删除地址
     * DELETE /address/{addressId}
     */
    @DeleteMapping("/{addressId}")
    public ResponseVO<String> deleteAddress(@PathVariable Long addressId) {
        try {
            userAddressService.deleteById(addressId);
            return getSuccessResponseVO("删除成功");
        } catch (Exception e) {
            log.error("删除地址失败: {}", e.getMessage(), e);
            return getFailResponseVO("删除地址失败: " + e.getMessage());
        }
    }

    /**
     * 修改收货地址
     * PUT /address/update
     */
    @PutMapping("/update")
    public ResponseVO<String> updateAddress(@Valid @RequestBody UserAddress userAddress) {
        try {
            userAddressService.updateAddress(userAddress);
            return getSuccessResponseVO("修改成功");
        } catch (Exception e) {
            log.error("修改地址失败: {}", e.getMessage(), e);
            return getFailResponseVO("修改地址失败: " + e.getMessage());
        }
    }

    /**
     * 根据地址ID查询地址详情
     * GET /address/{addressId}
     */
    @GetMapping("/{addressId}")
    public ResponseVO<UserAddress> getAddressById(@PathVariable Long addressId) {
        try {
            UserAddress address = userAddressService.getById(addressId);
            if (address == null) {
                return getFailResponseVO("地址不存在");
            }
            return getSuccessResponseVO(address);
        } catch (Exception e) {
            log.error("查询地址失败: {}", e.getMessage(), e);
            return getFailResponseVO("查询地址失败: " + e.getMessage());
        }
    }

    /**
     * 查询用户所有地址列表
     * GET /address/list?userId=xxx
     */
    @GetMapping("/list")
    public ResponseVO<List<UserAddress>> listByUserId(@RequestParam Long userId) {
        try {
            List<UserAddress> list = userAddressService.listByUserId(userId);
            return getSuccessResponseVO(list);
        } catch (Exception e) {
            log.error("查询地址列表失败: {}", e.getMessage(), e);
            return getFailResponseVO("查询地址列表失败: " + e.getMessage());
        }
    }
}
