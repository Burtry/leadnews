package com.heima.behavior.controller;
 
import com.heima.behavior.service.ApLikesBehaviorService;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.behavior.LikesBehaviorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping("/api/v1/likes_behavior")
public class ApLikesBehaviorController {
    @Autowired
    private ApLikesBehaviorService apLikesBehaviorService;
 
    /**
     * 点赞
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseResult like(@RequestBody LikesBehaviorDto dto) {
        return apLikesBehaviorService.like(dto);
    }
 
}