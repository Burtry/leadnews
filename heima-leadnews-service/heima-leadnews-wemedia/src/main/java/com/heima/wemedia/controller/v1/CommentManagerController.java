package com.heima.wemedia.controller.v1;
 

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.comment.*;
import com.heima.wemedia.service.CommentManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
 
@RestController
@RequestMapping("/api/v1/comment/manage")
public class CommentManagerController {
 
    @Autowired
    private CommentManagerService commentManagerService;
 
    /**
     * 查询评论列表
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult list(@RequestBody CommentManageDto dto) {
        return commentManagerService.list(dto);
    }
 
    /**
     * 删除评论
     * @param commentId
     * @return
     */
    @DeleteMapping("/del_comment/{commentId}")
    public ResponseResult delComment(@PathVariable("commentId") String commentId) {
        return commentManagerService.delComment(commentId);
    }
 
    /**
     * 删除评论回复
     * @param commentRepayId
     * @return
     */
    @DeleteMapping("/del_comment_repay/{commentRepayId}")
    public ResponseResult delCommentRepay(@PathVariable("commentRepayId") String commentRepayId) {
        return commentManagerService.delCommentRepay(commentRepayId);
    }
 
    /**
     * 分页查询文章评论列表
     * @param dto
     * @return
     */
    @PostMapping("/find_news_comments")
    public PageResponseResult findNewsComments(@RequestBody ArticleCommentDto dto) {
        return commentManagerService.findNewsComments(dto);
    }
 
    /**
     * 修改文章评论配置 打开或关闭评论
     * @param dto
     * @return
     */
    @PostMapping("/update_comment_status")
    public ResponseResult updateCommentStatus(@RequestBody CommentConfigDto dto) {
        return commentManagerService.updateCommentStatus(dto);
    }
 
 
    /**
     * 回复评论
     * @param dto
     * @return
     */
    @PostMapping("/comment_repay")
    public ResponseResult saveCommentRepay(@RequestBody CommentRepaySaveDto dto) {
        return commentManagerService.saveCommentRepay(dto);
    }
 
    /**
     * 点赞评论
     * @param dto
     * @return
     */
    @PostMapping("/like")
    public ResponseResult like(@RequestBody CommentLikeDto dto) {
        return commentManagerService.like(dto);
    }
}