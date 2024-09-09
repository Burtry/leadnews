package com.heima.model.dto.comment;
 
import lombok.Data;
 
@Data
public class CommentRepaySaveDto {
 
    /**
     * 评论id
     */
    private String commentId;
 
    /**
     * 回复内容
     */
    private String content;
}