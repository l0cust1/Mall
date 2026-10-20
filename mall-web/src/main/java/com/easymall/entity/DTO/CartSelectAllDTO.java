package com.easymall.entity.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 全选/取消全选
 */
@Data
public class CartSelectAllDTO {

    @NotNull(message = "勾选状态不能为空")
    private Boolean selected;
}
