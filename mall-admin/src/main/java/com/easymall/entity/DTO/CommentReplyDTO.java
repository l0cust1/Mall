package com.easymall.entity.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 评论回复请求DTO
 */
@Data
public class CommentReplyDTO {

    /**
     * 评论ID
     */
    @NotBlank(message = "评论ID不能为空")
    private String commentId;

    /**
     * 回复内容
     */
    @NotBlank(message = "回复内容不能为空")
    private String replyContent;
}
