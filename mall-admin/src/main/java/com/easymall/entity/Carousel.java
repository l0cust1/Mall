package com.easymall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("carousel")
@Schema(description = "轮播图实体")
public class Carousel {

    @Schema(description = "主键ID")
    @TableId(type = IdType.AUTO)
    private Long id;

    @Schema(description = "轮播图标题", example = "夏季大促")
    private String title;

    @Schema(description = "轮播图图片URL", example = "https://cdn.easymall.com/carousel/banner1.jpg")
    private String imageUrl;

    @Schema(description = "跳转链接URL")
    private String linkUrl;

    @Schema(description = "链接类型：0-无跳转 1-H5链接 2-商品详情 3-分类列表")
    private Integer linkType;

    @Schema(description = "关联ID（商品ID或分类ID）")
    private Long linkId;

    @Schema(description = "排序权重（值越大越靠前）", example = "100")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用 1-启用", example = "1")
    private Integer status;
    // ... 时间字段略
}