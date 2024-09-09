package com.heima.user.feign;

import com.heima.apis.user.IUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.pojo.user.ApUser;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserClient implements IUserClient {

    @Autowired
    private ApUserService apUserService;

    @PostMapping("/api/v1/user/save")
    @Override
    public ResponseResult save(@RequestBody ApUser user) {
        apUserService.save(user);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @GetMapping("/api/v1/user/findUserById/{id}")
    @Override
    public ApUser findUserById(@PathVariable("id") Integer id) {
        return apUserService.getById(id);
    }
}
