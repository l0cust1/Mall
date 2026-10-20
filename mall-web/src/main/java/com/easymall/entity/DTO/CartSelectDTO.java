package com.easymall.entity.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 勾选/取消勾选单个购物车项
 */
@Data
public class CartSelectDTO {

    @NotNull(message = "购物车项ID不能为空")
    private Long cartItemId;

    @NotNull(message = "勾选状态不能为空")
    private Boolean selected;
}
