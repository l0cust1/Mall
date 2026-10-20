package com.easymall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("user_address")
public class UserAddress {

    @TableId(value = "address_id", type = IdType.AUTO)
    private Long addressId;

    /**
     * 所属用户ID
     */
    private Long userId;

    /**
     * 收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 收货人手机号
     */
    @NotBlank(message = "收货人手机号不能为空")
    private String receiverPhone;

    /**
     * 省份
     */
    @NotBlank(message = "省份不能为空")
    private String province;

    /**
     * 城市
     */
    @NotBlank(message = "城市不能为空")
    private String city;

    /**
     * 区/县
     */
    @NotBlank(message = "区/县不能为空")
    private String district;

    /**
     * 详细收货地址
     */
    @NotBlank(message = "详细地址不能为空")
    private String detailAddress;

    /**
     * 是否默认地址：1默认，0非默认
     */
    private Integer isDefault;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updateTime;
}
