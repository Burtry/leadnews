package com.heima.wemedia.service;
 

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.comment.*;

public interface CommentManagerService {
    /**
     * 查询评论列表
     * @param dto
     * @return
     */
    ResponseResult list(CommentManageDto dto);
 
    /**
     * 删除评论
     * @param commentId
     * @return
     */
    ResponseResult delComment(String commentId);
 
    /**
     * 删除评论回复
     * @param commentRepayId
     * @return
     */
    ResponseResult delCommentRepay(String commentRepayId);
 
    /**
     * 分页查询文章评论列表
     * @param dto
     * @return
     */
    PageResponseResult findNewsComments(ArticleCommentDto dto);
 
    /**
     * 修改文章评论配置 打开或关闭评论
     * @param dto
     * @return
     */
    ResponseResult updateCommentStatus(CommentConfigDto dto);
 
    /**
     * 回复评论
     * @param dto
     * @return
     */
    ResponseResult saveCommentRepay(CommentRepaySaveDto dto);
 
    /**
     * 点赞评论
     * @param dto
     * @return
     */
    ResponseResult like(CommentLikeDto dto);
}