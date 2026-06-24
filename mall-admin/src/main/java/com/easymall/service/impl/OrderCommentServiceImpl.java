package com.easymall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.easymall.entity.DTO.CommentReplyDTO;
import com.easymall.entity.Order.OrderComment;
import com.easymall.entity.OrderCommentQuery;
import com.easymall.mapper.OrderCommentMapper;
import com.easymall.service.OrderCommentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 订单评论服务实现类
 */
@Service
public class OrderCommentServiceImpl extends ServiceImpl<OrderCommentMapper, OrderComment> implements OrderCommentService {

    /**
     * 分页查询评论列表
     */
    @Override
    public IPage<OrderComment> getCommentPage(OrderCommentQuery query) {
        // 1. 构建分页对象
        Page<OrderComment> page = new Page<>(query.getPageNum(), query.getPageSize());

        // 2. 构建查询条件
        LambdaQueryWrapper<OrderComment> wrapper = new LambdaQueryWrapper<>();
        
        // 只查询未删除的评论
        wrapper.eq(OrderComment::getIsDeleted, 0);
        
        // 用户昵称模糊查询
        if (StringUtils.hasText(query.getUserName())) {
            wrapper.like(OrderComment::getUserName, query.getUserName());
        }
        
        // 商品名称模糊查询
        if (StringUtils.hasText(query.getProductName())) {
            wrapper.like(OrderComment::getProductName, query.getProductName());
        }
        
        // 按评论时间降序
        wrapper.orderByDesc(OrderComment::getCommentTime);

        // 3. 执行分页查询
        return this.page(page, wrapper);
    }

    /**
     * 删除评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(String commentId) {
        OrderComment comment = this.getById(commentId);
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 逻辑删除
        comment.setIsDeleted(1);
        comment.setUpdateTime(LocalDateTime.now());
        this.updateById(comment);
    }

    /**
     * 商家回复评论
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyComment(CommentReplyDTO replyDTO) {
        OrderComment comment = this.getById(replyDTO.getCommentId());
        if (comment == null) {
            throw new RuntimeException("评论不存在");
        }
        
        // 更新回复内容
        comment.setReplyContent(replyDTO.getReplyContent());
        comment.setReplyTime(LocalDateTime.now());
        comment.setUpdateTime(LocalDateTime.now());
        
        this.updateById(comment);
    }
}
