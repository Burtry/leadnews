package com.heima.user.controller.v1;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.dtos.UserAuthPageRequestDTO;
import com.heima.user.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class UserAuthController {


    @Autowired
    private UserAuthService userAuthService;

    @PostMapping("/list")
    public ResponseResult getList(@RequestBody UserAuthPageRequestDTO userAuthPageRequestDTO) {
        return userAuthService.getList(userAuthPageRequestDTO);
    }


    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody UserAuthPageRequestDTO userAuthPageRequestDTO) {
        return userAuthService.authPass(userAuthPageRequestDTO);
    }


    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody UserAuthPageRequestDTO userAuthPageRequestDTO) {
        return userAuthService.authFail(userAuthPageRequestDTO);
    }
}
