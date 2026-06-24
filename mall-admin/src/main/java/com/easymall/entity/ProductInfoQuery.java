package com.easymall.entity;

import lombok.Data;

@Data
public class ProductInfoQuery {
    private String productName;
    private Integer status;
    private Integer CommendType;

    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;

}
