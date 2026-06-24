package com.easymall.entity.constants.vo;

import lombok.Data;



@Data
public class ResponseVO<T> {
        private int code;       // 状态码：200成功，500失败
        private String message; // 提示信息
        private T data;         // 响应数据（泛型）
    }

