package com.easymall.entity.VO;


import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class HomeAggregateVO implements Serializable {
    /** 轮播图列表 */
    private List<CarouselVO> carousels;

    /** 一级分类导航列表 */
    private List<CategoryVO> categories;

    /** 推荐商品列表 */
    private List<RecommendProductVO> recommendProducts;

    /** 秒杀活动信息 */
    private SeckillVO seckill;

    /** 用户浏览历史（未登录返回空列表） */
    private List<BrowseHistoryVO> browseHistory;
}