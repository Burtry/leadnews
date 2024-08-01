package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.wemedia.WmLoginDto;
import com.heima.model.pojo.wemedia.WmUser;


public interface WmUserService extends IService<WmUser> {

    /**
     * 自媒体端登录
     * @param dto
     * @return
     */
    ResponseResult login(WmLoginDto dto);

}