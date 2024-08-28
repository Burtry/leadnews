package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApCollectionMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ApCollectionService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.article.CollectionBehaviorDTO;
import com.heima.model.pojo.article.APCollection;
import com.heima.model.pojo.article.ApArticle;
import com.heima.model.pojo.user.ApUser;
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
public class ApCollectionServiceImpl extends ServiceImpl<ApCollectionMapper, APCollection> implements ApCollectionService {


    @Autowired
    private CacheService cacheService;


    @Autowired
    private ApArticleService articleService;


    @Override
    public ResponseResult collcetion(CollectionBehaviorDTO collectionBehaviorDTO) {
        if (collectionBehaviorDTO == null || collectionBehaviorDTO.getOperation() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        String collectionJson = (String) cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), collectionBehaviorDTO.getEntryId().toString());

        //查询已收藏
        if (StringUtils.isNotBlank(collectionJson) && collectionBehaviorDTO.getOperation() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"已收藏");
        }


        if (collectionBehaviorDTO.getOperation() == 0) {
            //收藏
            cacheService.hPut(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(),collectionBehaviorDTO.getEntryId().toString(),JSON.toJSONString(collectionBehaviorDTO));
        } else {
            //取消收藏
            cacheService.hDelete(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(),collectionBehaviorDTO.getEntryId().toString());
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

    }
}
