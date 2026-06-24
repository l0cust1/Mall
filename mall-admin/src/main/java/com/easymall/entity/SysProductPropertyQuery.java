package com.easymall.entity;

import lombok.Data;

/**
 * 商品属性查询参数
 */
@Data
public class SysProductPropertyQuery {
    
    /**
     * 页码
     */
    private Integer pageNum = 1;
    
    /**
     * 每页大小
     */
    private Integer pageSize = 10;
    

}
