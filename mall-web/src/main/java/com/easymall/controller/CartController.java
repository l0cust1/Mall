package com.easymall.controller;

import com.easymall.entity.DTO.CartAddDTO;
import com.easymall.entity.DTO.CartQuantityDTO;
import com.easymall.entity.DTO.CartSelectAllDTO;
import com.easymall.entity.DTO.CartSelectDTO;
import com.easymall.entity.SysUser;
import com.easymall.entity.VO.CartSummaryVO;
import com.easymall.result.ApiResult;
import com.easymall.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户购物车控制器
 */
@Tag(name = "购物车模块", description = "用户购物车增删改查")
@RestController
@RequestMapping("/cart")
@Slf4j
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 加入购物车
     * POST /cart/add
     */
    @Operation(summary = "加入购物车", description = "同一用户同一 SKU 会自动合并数量")
    @PostMapping("/add")
    public ApiResult<Void> addItem(@Valid @RequestBody CartAddDTO dto,
                                   HttpServletRequest request) {
        try {
            cartService.addItem(getUserId(request), dto);
            return ApiResult.success(null, "加入购物车成功");
        } catch (RuntimeException e) {
            log.warn("加入购物车失败: {}", e.getMessage());
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("加入购物车异常", e);
            return ApiResult.error(500, "加入购物车失败，请稍后重试");
        }
    }

    /**
     * 查询购物车
     * GET /cart/list
     */
    @Operation(summary = "查询购物车", description = "返回购物车列表、勾选数量和选中金额")
    @GetMapping("/list")
    public ApiResult<CartSummaryVO> listItems(HttpServletRequest request) {
        try {
            return ApiResult.success(cartService.listItems(getUserId(request)));
        } catch (RuntimeException e) {
            log.warn("查询购物车失败: {}", e.getMessage());
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询购物车异常", e);
            return ApiResult.error(500, "查询购物车失败，请稍后重试");
        }
    }

    /**
     * 修改购物车商品数量
     * PUT /cart/quantity
     */
    @Operation(summary = "修改购物车数量")
    @PutMapping("/quantity")
    public ApiResult<Void> updateQuantity(@Valid @RequestBody CartQuantityDTO dto,
                                          HttpServletRequest request) {
        try {
            cartService.updateQuantity(getUserId(request), dto);
            return ApiResult.success(null, "数量修改成功");
        } catch (RuntimeException e) {
            log.warn("修改购物车数量失败: {}", e.getMessage());
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("修改购物车数量异常", e);
            return ApiResult.error(500, "修改购物车数量失败，请稍后重试");
        }
    }

    /**
     * 勾选/取消勾选单个购物车项
     * PUT /cart/select
     */
    @Operation(summary = "勾选购物车项")
    @PutMapping("/select")
    public ApiResult<Void> updateSelected(@Valid @RequestBody CartSelectDTO dto,
                                          HttpServletRequest request) {
        try {
            cartService.updateSelected(getUserId(request), dto);
            return ApiResult.success(null, "勾选状态更新成功");
        } catch (RuntimeException e) {
            log.warn("更新购物车勾选状态失败: {}", e.getMessage());
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新购物车勾选状态异常", e);
            return ApiResult.error(500, "更新勾选状态失败，请稍后重试");
        }
    }

    /**
     * 全选/取消全选
     * PUT /cart/selectAll
     */
    @Operation(summary = "全选/取消全选")
    @PutMapping("/selectAll")
    public ApiResult<Void> selectAll(@Valid @RequestBody CartSelectAllDTO dto,
                                     HttpServletRequest request) {
        try {
            cartService.selectAll(getUserId(request), dto);
            return ApiResult.success(null, "全选状态更新成功");
        } catch (RuntimeException e) {
            log.warn("更新购物车全选状态失败: {}", e.getMessage());
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("更新购物车全选状态异常", e);
            return ApiResult.error(500, "更新全选状态失败，请稍后重试");
        }
    }

    /**
     * 删除单个购物车项
     * DELETE /cart/{cartItemId}
     */
    @Operation(summary = "删除购物车项")
    @DeleteMapping("/{cartItemId}")
    public ApiResult<Void> removeItem(@PathVariable Long cartItemId,
                                      HttpServletRequest request) {
        try {
            cartService.removeItem(getUserId(request), cartItemId);
            return ApiResult.success(null, "删除成功");
        } catch (RuntimeException e) {
            log.warn("删除购物车项失败: {}", e.getMessage());
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("删除购物车项异常", e);
            return ApiResult.error(500, "删除失败，请稍后重试");
        }
    }

    /**
     * 清空购物车
     * DELETE /cart/clear
     */
    @Operation(summary = "清空购物车")
    @DeleteMapping("/clear")
    public ApiResult<Void> clear(HttpServletRequest request) {
        try {
            cartService.clear(getUserId(request));
            return ApiResult.success(null, "购物车已清空");
        } catch (RuntimeException e) {
            log.warn("清空购物车失败: {}", e.getMessage());
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("清空购物车异常", e);
            return ApiResult.error(500, "清空购物车失败，请稍后重试");
        }
    }

    /**
     * 购物车商品数量（导航栏角标）
     * GET /cart/count
     */
    @Operation(summary = "购物车商品数量")
    @GetMapping("/count")
    public ApiResult<Integer> count(HttpServletRequest request) {
        try {
            return ApiResult.success(cartService.countItems(getUserId(request)));
        } catch (RuntimeException e) {
            log.warn("查询购物车数量失败: {}", e.getMessage());
            return ApiResult.error(400, e.getMessage());
        } catch (Exception e) {
            log.error("查询购物车数量异常", e);
            return ApiResult.error(500, "查询购物车数量失败，请稍后重试");
        }
    }

    private Long getUserId(HttpServletRequest request) {
        SysUser user = (SysUser) request.getAttribute("currentUser");
        if (user == null) {
            throw new RuntimeException("用户未登录");
        }
        return Long.valueOf(user.getUserId());
    }
}
