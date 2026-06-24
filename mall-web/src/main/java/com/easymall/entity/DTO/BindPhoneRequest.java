package com.easymall.entity.DTO;

import lombok.Data;

@Data
public class BindPhoneRequest {
    private String phone;
    private String code; // 验证码
}