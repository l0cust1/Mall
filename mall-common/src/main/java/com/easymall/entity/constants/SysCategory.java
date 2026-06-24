package com.easymall.entity.constants;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_category")
public class SysCategory {
    // 主键类型改为 String，type 改为 NONE（非自增）
    @TableId(value = "category_id", type = IdType.NONE)
    private String categoryId;

    private String categoryName;
    private String pCategoryId; // 数据库里 p_category_id 也存了字符串 002，建议也改成 String
    private Integer sort;
}