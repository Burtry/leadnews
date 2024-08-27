package com.heima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.user.UserRelationDto;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.pojo.user.ApUserFollow;
import com.heima.user.mapper.UserBehaviorMapper;
import com.heima.user.service.ApUserService;
import com.heima.user.service.UserBehaviorService;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Slf4j
@Transactional
public class UserBehaviorServiceImpl extends ServiceImpl<UserBehaviorMapper, ApUserFollow> implements UserBehaviorService {

    @Autowired
    private ApUserService apUserService;

    @Autowired
    private CacheService cacheService;


    @Override
    public ResponseResult userFollow(UserRelationDto userRelationDto) {
        if (userRelationDto == null || userRelationDto.getOperation() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //获取用户信息
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        Integer apUserId = user.getId();

        Integer followUserId = userRelationDto.getAuthorId();

        if (userRelationDto.getOperation() == 0) {
            //将对方写入我的关注中
            cacheService.zAdd(BehaviorConstants.APUSER_FOLLOW_RELATION + apUserId,followUserId.toString(),System.currentTimeMillis());

            //将我写入对方粉丝
            cacheService.zAdd(BehaviorConstants.APUSER_FANS_RELATION + followUserId,apUserId.toString(),System.currentTimeMillis());

        } else {
            //取消关注
            cacheService.zRemove(BehaviorConstants.APUSER_FOLLOW_RELATION + apUserId,followUserId.toString());
            cacheService.zRemove(BehaviorConstants.APUSER_FANS_RELATION + followUserId,apUserId.toString());

        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }
}
