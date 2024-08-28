package com.heima.behavior.controller;


import com.heima.behavior.service.ApReadBehaviorService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.behavior.ReadBehaviorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
 
@RestController
@RequestMapping("/api/v1/read_behavior")
public class ApReadBehaviorController {
    @Autowired
    private ApReadBehaviorService apReadBehaviorService;
 
    /**
     * 用户行为-阅读
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseResult readBehavior(@RequestBody ReadBehaviorDto dto) {
        return apReadBehaviorService.readBehavior(dto);
    }
}