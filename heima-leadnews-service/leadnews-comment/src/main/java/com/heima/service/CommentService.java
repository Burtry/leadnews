package com.heima.service;
 

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.comment.CommentDto;
import com.heima.model.dto.comment.CommentLikeDto;
import com.heima.model.dto.comment.CommentSaveDto;

public interface CommentService {
    /**
     * 保存评论
     * @param dto
     * @return
     */
    ResponseResult saveComment(CommentSaveDto dto);
 
    /**
     * 点赞评论
     * @param dto
     * @return
     */
    ResponseResult like(CommentLikeDto dto);
 
    /**
     * 加载评论列表
     * @param dto
     * @return
     */
    ResponseResult findByArticleId(CommentDto dto);
}