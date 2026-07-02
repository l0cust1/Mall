package com.easymall.entity.VO;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "首页聚合数据响应体")
public class HomeAggregateVO implements Serializable {

    @Schema(description = "轮播图列表")
    private List<CarouselVO> carousels;

    @Schema(description = "一级分类导航列表")
    private List<CategoryVO> categories;

    @Schema(description = "推荐商品列表")
    private List<RecommendProductVO> recommendProducts;

    @Schema(description = "秒杀活动信息")
    private SeckillVO seckill;

    @Schema(description = "用户浏览历史（未登录返回空列表）")
    private List<BrowseHistoryVO> browseHistory;
}