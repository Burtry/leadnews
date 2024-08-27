package com.heima.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.user.UserRelationDto;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.pojo.user.ApUserFollow;

public interface UserBehaviorService extends IService<ApUserFollow> {
    /**
     * 用户关注与取消关注
     * @param userRelationDto
     * @return
     */
    ResponseResult userFollow(UserRelationDto userRelationDto);
}
