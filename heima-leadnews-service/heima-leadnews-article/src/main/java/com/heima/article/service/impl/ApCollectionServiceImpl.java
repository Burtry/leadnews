package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApCollectionMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ApCollectionService;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.article.CollectionBehaviorDTO;
import com.heima.model.pojo.article.APCollection;
import com.heima.model.pojo.article.ApArticle;
import com.heima.model.pojo.user.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
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
        if (collectionBehaviorDTO == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();

        //获取文章
        ApArticle apArticle = articleService.getById(collectionBehaviorDTO.getEntryId());
        if (collectionBehaviorDTO.getOperation() == 0) {
            //    收藏
            // 存储在redis
            //获取用户信息

            APCollection apCollection = new APCollection();
            apCollection.setEntryId(collectionBehaviorDTO.getEntryId());
            apCollection.setType(collectionBehaviorDTO.getType());
            apCollection.setCollectionTime(new Date());
            apCollection.setPublishedTime(collectionBehaviorDTO.getPublishedTime());

            //存储到redis 中 4为userId
            cacheService.set("collection_userId_" + 4 + "_articleId_" + apArticle.getId(), JSON.toJSONString(apCollection));

            return ResponseResult.okResult(200,"收藏成功");
        } else {
            //取消收藏 //根据key删除redis中的数据
            cacheService.delete("collection_userId_" + 4 + "_articleId_" + apArticle.getId());
            return ResponseResult.okResult(200,"取消收藏成功");
        }

    }
}
