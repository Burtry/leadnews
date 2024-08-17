package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.dtos.UserAuthPageRequestDTO;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.pojo.user.ApUserRealname;

public interface UserAuthService extends IService<ApUserRealname> {

    /**
     * 分页获取用户认证列表
     * @param
     * @return
     */
    ResponseResult getList(UserAuthPageRequestDTO userAuthPageRequestDTO);

    /**
     * 审核通过
     * @param userAuthPageRequestDTO
     * @return
     */
    ResponseResult authPass(UserAuthPageRequestDTO userAuthPageRequestDTO);

    /**
     * 审核失败
     * @param userAuthPageRequestDTO
     * @return
     */
    ResponseResult authFail(UserAuthPageRequestDTO userAuthPageRequestDTO);
}
