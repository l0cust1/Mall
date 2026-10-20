package com.easymall.entity.vo;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceVO {

   private BigDecimal minPrice;
   private BigDecimal maxPrice;
}
