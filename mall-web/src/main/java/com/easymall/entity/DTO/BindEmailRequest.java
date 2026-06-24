package com.easymall.entity.DTO;

import lombok.Data;

@Data
public class BindEmailRequest {
    private String email;
    private String code; // 验证码（需先发送邮箱验证码）
}