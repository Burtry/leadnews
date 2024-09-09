package com.heima.model.vo.comment;
 
import com.heima.model.pojo.comment.ApComment;
import lombok.Data;
 
@Data
public class CommentVo extends ApComment {
 
    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Short operation;
}