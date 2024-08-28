package com.heima.behavior.service;
 

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.behavior.ReadBehaviorDto;

public interface ApReadBehaviorService {
    /**
     * 用户行为 - 阅读
     * @param dto
     * @return
     */
    ResponseResult readBehavior(ReadBehaviorDto dto);
}