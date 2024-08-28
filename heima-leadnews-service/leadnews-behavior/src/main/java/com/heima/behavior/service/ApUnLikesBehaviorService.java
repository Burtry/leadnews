package com.heima.behavior.service;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.behavior.UnLikesBehaviorDto;

public interface ApUnLikesBehaviorService {
    /**
     * 用户行为 -不喜欢
     * @param unLikesBehaviorDto
     * @return
     */
    ResponseResult unlike(UnLikesBehaviorDto unLikesBehaviorDto);
}
