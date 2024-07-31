package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.LoginDTO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.pojo.ApUser;

public interface ApUserService extends IService<ApUser> {

    ResponseResult login(LoginDTO loginDTO);
}
