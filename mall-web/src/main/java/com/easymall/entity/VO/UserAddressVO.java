package com.easymall.entity.VO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户收货地址信息")
public class UserAddressVO {
    
    @Schema(description = "地址ID")
    private Long addressId;
    
    @Schema(description = "收货人姓名")
    private String receiverName;
    
    @Schema(description = "收货人手机号")
    private String receiverPhone;
    
    @Schema(description = "省份")
    private String province;
    
    @Schema(description = "城市")
    private String city;
    
    @Schema(description = "区/县")
    private String district;
    
    @Schema(description = "详细地址")
    private String detailAddress;
    
    @Schema(description = "是否默认地址")
    private Boolean isDefault;
}
