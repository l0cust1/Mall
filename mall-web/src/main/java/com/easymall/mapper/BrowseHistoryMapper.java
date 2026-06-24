package com.easymall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.easymall.entity.BrowseHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BrowseHistoryMapper extends BaseMapper<BrowseHistory> {
    /**
     * 查询用户最近浏览记录（按时间倒序，限制条数）
     */
    @Select("SELECT id, user_id, product_id, product_name, product_image, price, browse_time " +
            "FROM browse_history " +
            "WHERE user_id = #{userId} " +
            "ORDER BY browse_time DESC " +
            "LIMIT #{limit}")
    List<BrowseHistory> selectRecentByUserId(@Param("userId") Long userId,
                                             @Param("limit") int limit);
}