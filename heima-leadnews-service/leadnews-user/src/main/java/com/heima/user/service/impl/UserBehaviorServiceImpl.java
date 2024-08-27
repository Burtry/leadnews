package com.heima.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
        if (userRelationDto == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //获取用户信息
        ApUser user = AppThreadLocalUtil.getUser();

        user = apUserService.getById(4);


        //查询作者信息
        ApUser author = apUserService.getById(userRelationDto.getAuthorId());

        if (userRelationDto.getOperation() == 0) {
            //关注，新增
            ApUserFollow apUserFollow = new ApUserFollow();
            apUserFollow.setUserId(user.getId());
            apUserFollow.setFollowId(author.getId());
            apUserFollow.setFollowName(author.getName());
            apUserFollow.setCreatedTime(new Date());

            //存储到redis中 key: fallow_userId_id_authorId_id
            cacheService.set("fallow_userId_" + user.getId() + "authorId_" + author.getId(),JSON.toJSONString(apUserFollow));

            return ResponseResult.okResult(200, "关注成功");

        } else {
            cacheService.delete("fallow_userId_" + user.getId() + "authorId_" + author.getId());
            return ResponseResult.okResult(200,"取消关注成功");

        }


    }
}
