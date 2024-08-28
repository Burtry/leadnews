package com.heima.behavior.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.behavior.LikesBehaviorDto;

public interface ApLikesBehaviorService {

    /**
     * 点赞
     * @param dto
     * @return
     */
    ResponseResult like(LikesBehaviorDto dto);
}
