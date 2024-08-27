package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.user.UserRelationDto;
import com.heima.user.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService userBehaviorService;

    @PostMapping("/user/user_follow")
    public ResponseResult userFollow(@RequestBody UserRelationDto userRelationDto) {
        return userBehaviorService.userFollow(userRelationDto);
    }

}
