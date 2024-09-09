package com.heima.controller;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.comment.CommentRepayDto;
import com.heima.model.dto.comment.CommentRepayLikeDto;
import com.heima.model.dto.comment.CommentRepaySaveDto;
import com.heima.service.CommentRepayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment_repay")
public class CommentRepayController {

    @Autowired
    private CommentRepayService commentRepayService;

    @PostMapping("/load")
    public ResponseResult load(@RequestBody CommentRepayDto commentRepayDto) {
        return commentRepayService.load(commentRepayDto);
    }

    @PostMapping("/save")
    public ResponseResult save(@RequestBody CommentRepaySaveDto dto) {
        return commentRepayService.save(dto);
    }

    @PostMapping("/like")
    public ResponseResult like(@RequestBody CommentRepayLikeDto commentRepayLikeDto) {
        return commentRepayService.like(commentRepayLikeDto);
    }



}
