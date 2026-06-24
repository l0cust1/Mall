package com.easymall.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.easymall.entity.DTO.CommentReplyDTO;
import com.easymall.entity.Order.OrderComment;
import com.easymall.entity.OrderCommentQuery;
import com.easymall.entity.constants.vo.ResponseVO;
import com.easymall.service.OrderCommentService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 订单评论管理Controller
 */
@RestController
@RequestMapping("/comment")
public class CommentController extends ABaseController {

    @Resource
    private OrderCommentService orderCommentService;

    /**
     * 分页查询评论列表
     * @param query 查询条件
     * @return 评论分页数据
     */
    @GetMapping("/list")
    public ResponseVO getCommentList(OrderCommentQuery query) {
        IPage<OrderComment> page = orderCommentService.getCommentPage(query);
        return getSuccessResponseVO(page);
    }

    /**
     * 删除评论
     * @param commentId 评论ID
     * @return 操作结果
     */
    @PostMapping("/delete")
    public ResponseVO deleteComment(@RequestParam String commentId) {
        try {
            orderCommentService.deleteComment(commentId);
            return getSuccessResponseVO("删除成功");
        } catch (Exception e) {
            return getFailResponseVO(e.getMessage());
        }
    }

    /**
     * 商家回复评论
     * @param replyDTO 回复信息
     * @return 操作结果
     */
    @PostMapping("/reply")
    public ResponseVO replyComment(@Valid @RequestBody CommentReplyDTO replyDTO) {
        try {
            orderCommentService.replyComment(replyDTO);
            return getSuccessResponseVO("回复成功");
        } catch (Exception e) {
            return getFailResponseVO(e.getMessage());
        }
    }
}
