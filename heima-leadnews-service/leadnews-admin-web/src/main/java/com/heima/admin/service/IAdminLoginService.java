package com.heima.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.AdminLoginDTO;
import com.heima.model.pojo.admin.AdUser;
import com.heima.model.common.dtos.ResponseResult;

public interface IAdminLoginService extends IService<AdUser> {


    /**
     * 管理端登录
     * @param adminLoginDTO
     * @return
     */
    ResponseResult login(AdminLoginDTO adminLoginDTO);
}
