package com.easymall.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.easymall.entity.DTO.CommentReplyDTO;
import com.easymall.entity.Order.OrderComment;
import com.easymall.entity.OrderCommentQuery;

/**
 * 订单评论服务接口
 */
public interface OrderCommentService extends IService<OrderComment> {
    
    /**
     * 分页查询评论列表
     * @param query 查询条件
     * @return 评论分页数据
     */
    IPage<OrderComment> getCommentPage(OrderCommentQuery query);
    
    /**
     * 删除评论
     * @param commentId 评论ID
     */
    void deleteComment(String commentId);
    
    /**
     * 商家回复评论
     * @param replyDTO 回复信息
     */
    void replyComment(CommentReplyDTO replyDTO);
}
