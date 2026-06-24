package com.easymall.entity.VO;



import lombok.Data;
import java.io.Serializable;

@Data
public class CategoryVO implements Serializable {
    private String categoryId;      // 注意：这里是 String 类型
    private String categoryName;
    private String pCategoryId;     // 前端可能需要，也可以不返回
    private Integer sort;
}