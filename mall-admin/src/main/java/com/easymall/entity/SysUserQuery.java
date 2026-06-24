package com.easymall.entity;

import lombok.Data;

@Data
public class SysUserQuery {
    private String nickname;
    private Integer status;
    private Integer pageNum = 1;
    private Integer pageSize = 15;
}
