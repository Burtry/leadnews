package com.heima.behavior.controller;

import com.heima.behavior.service.ApUnLikesBehaviorService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.article.ArticleDto;
import com.heima.model.dto.behavior.UnLikesBehaviorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApUnLikesBehaviorController {

    @Autowired
    private ApUnLikesBehaviorService apUnLikesBehaviorService;

    @PostMapping("/un_likes_behavior")
    public ResponseResult unlike(@RequestBody UnLikesBehaviorDto unLikesBehaviorDto) {
        return apUnLikesBehaviorService.unlike(unLikesBehaviorDto);
    }


}
