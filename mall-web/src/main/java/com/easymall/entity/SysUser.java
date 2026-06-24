package com.easymall.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(value = "user_id", type = IdType.NONE)
    private String userId;

    private String username;

    private Integer gender; // 0-未设置，1-男，2-女

    private String nickname;

    private String avatar;

    private String email;

    private String phone;

    private String password;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime joinTime;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer isDeleted;
}
