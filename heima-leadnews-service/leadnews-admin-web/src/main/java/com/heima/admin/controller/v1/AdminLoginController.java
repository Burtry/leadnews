package com.heima.admin.controller.v1;

import com.heima.model.common.dtos.AdminLoginDTO;
import com.heima.admin.service.IAdminLoginService;
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
    private IAdminLoginService IAdminLoginService;

    @PostMapping("/in")
    public ResponseResult login(@RequestBody AdminLoginDTO adminLoginDTO) {


        return IAdminLoginService.login(adminLoginDTO);
    }
}
