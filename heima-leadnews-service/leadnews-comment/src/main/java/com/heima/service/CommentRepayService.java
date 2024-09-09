package com.heima.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.comment.CommentRepayDto;
import com.heima.model.dto.comment.CommentRepayLikeDto;
import com.heima.model.dto.comment.CommentRepaySaveDto;

public interface CommentRepayService {
    /**\
     * 查询评论回复列表
     * @param commentRepayDto
     * @return
     */
    ResponseResult load(CommentRepayDto commentRepayDto);

    /**
     * 保存评论的回复
     * @param dto
     * @return
     */
    ResponseResult save(CommentRepaySaveDto dto);

    /**
     * 点赞回复的评论
     * @param commentRepayLikeDto
     * @return
     */
    ResponseResult like(CommentRepayLikeDto commentRepayLikeDto);
}
