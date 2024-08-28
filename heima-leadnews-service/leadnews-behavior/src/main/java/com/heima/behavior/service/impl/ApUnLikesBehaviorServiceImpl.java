package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.ApUnLikesBehaviorService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.behavior.UnLikesBehaviorDto;
import com.heima.model.pojo.user.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ApUnLikesBehaviorServiceImpl implements ApUnLikesBehaviorService {

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult unlike(UnLikesBehaviorDto unLikesBehaviorDto) {
        if (unLikesBehaviorDto == null || unLikesBehaviorDto.getArticleId() == null || unLikesBehaviorDto.getType() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //用户查询
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        if (unLikesBehaviorDto.getType() == 0) {
            //不喜欢
            cacheService.hPut(BehaviorConstants.UN_LIKE_BEHAVIOR + unLikesBehaviorDto.getArticleId(), user.getId().toString(), JSON.toJSONString(unLikesBehaviorDto));
        } else {
            //取消不喜欢
            cacheService.hDelete(BehaviorConstants.UN_LIKE_BEHAVIOR + unLikesBehaviorDto.getArticleId(),user.getId().toString());
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }
}
