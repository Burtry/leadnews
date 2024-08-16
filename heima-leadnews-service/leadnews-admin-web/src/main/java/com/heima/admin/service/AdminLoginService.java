package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.admin.dto.AdminLoginDTO;
import com.heima.admin.pojo.AdUser;
import com.heima.model.common.dtos.ResponseResult;

public interface AdminLoginService extends IService<AdUser> {


    /**
     * 管理端登录
     * @param adminLoginDTO
     * @return
     */
    ResponseResult login(AdminLoginDTO adminLoginDTO);
}
