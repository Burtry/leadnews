package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.ApLikesBehaviorService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.constants.HotArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.behavior.LikesBehaviorDto;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.model.pojo.user.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ApLikesBehaviorServiceImpl implements ApLikesBehaviorService {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private KafkaTemplate<String,String> kafkaTemplate;

    @Override
    public ResponseResult like(LikesBehaviorDto dto) {
        if (dto == null || dto.getArticleId() == null || checkParam(dto)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        UpdateArticleMess updateArticleMess = new UpdateArticleMess();
        updateArticleMess.setArticleId(dto.getArticleId());
        updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.LIKES);

        if (dto.getOperation() == 0) {
            //点赞
            Object object = cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());

            if (object != null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "已点赞");
            }
            //向redis保存当前key
            cacheService.hPut(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId(),user.getId().toString(), JSON.toJSONString(dto));
            //updateArticleMess.setAdd(1);
        } else {
            //取消点赞
            cacheService.hDelete(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId(),user.getId().toString());
            //updateArticleMess.setAdd(-1);
        }

        //数据聚合
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(updateArticleMess));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 检查参数
     * @param dto
     * @return
     */
    private boolean checkParam(LikesBehaviorDto dto) {
        // 参数有误
        if(dto.getType() > 2 || dto.getType() < 0 || dto.getOperation() > 1 || dto.getOperation() < 0) {
            return true;
        }

        return false;
    }

}
