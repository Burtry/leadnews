package com.heima.admin.controller;

import com.heima.admin.dto.AdminLoginDTO;
import com.heima.admin.service.AdminLoginService;
import com.heima.model.common.dtos.LoginDTO;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@Slf4j
public class AdminLoginController {

    @Autowired
    private AdminLoginService adminLoginService;

    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdminLoginDTO adminLoginDTO) {


        return adminLoginService.login(adminLoginDTO);
    }
}
