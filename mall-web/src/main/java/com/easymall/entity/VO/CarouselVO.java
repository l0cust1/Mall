package com.easymall.entity.VO;


import lombok.Data;
import java.io.Serializable;

@Data
public class CarouselVO implements Serializable {
    private Long id;
    private String title;
    private String imageUrl;
    private String linkUrl;
    private Integer sortOrder;
}