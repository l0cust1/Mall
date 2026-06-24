package com.easymall.service;


import com.easymall.entity.BrowseHistory;
import com.easymall.entity.VO.BrowseHistoryVO;

import java.util.List;

public interface BrowseHistoryService {

    /**
     * 记录用户浏览行为（商品详情页调用）
     */
    void recordBrowse(Long userId, Long productId);

    /**
     * 获取用户最近浏览历史（首页用，带缓存）
     * @param userId 用户ID（未登录传null）
     * @return 最近浏览列表，未登录返回空列表
     */
    List<BrowseHistoryVO> getRecentHistory(Long userId);

    /**
     * 根据ID查询浏览记录
     */
    BrowseHistory getById(Long id);

    /**
     * 删除单条浏览记录
     */
    void deleteById(Long id);

    /**
     * 清空用户所有浏览历史
     */
    void clearByUserId(Long userId);

    /**
     * 手动清除缓存
     */
    void clearCache(Long userId);
}