package com.heima.user.controller.v1;


import com.heima.model.common.dtos.LoginDTO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
public class ApUserLoginController {

    @Autowired
    private ApUserService userService;

    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDTO loginDTO) {

        return userService.login(loginDTO);
    }
}
