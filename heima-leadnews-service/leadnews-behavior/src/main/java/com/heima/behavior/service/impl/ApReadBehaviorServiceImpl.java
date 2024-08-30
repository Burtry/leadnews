package com.heima.behavior.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.behavior.service.ApReadBehaviorService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.constants.HotArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.behavior.ReadBehaviorDto;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.model.pojo.user.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ApReadBehaviorServiceImpl implements ApReadBehaviorService {


    @Autowired
    private CacheService cacheService;

    @Autowired
    private KafkaTemplate kafkaTemplate;
    @Override
    public ResponseResult readBehavior(ReadBehaviorDto dto) {
        // 1. 检查参数
        if(dto == null || dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 2. 是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        // 3. 更新阅读次数
        String readBehaviorJson = (String) cacheService.hGet(BehaviorConstants.READ_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
        if(StringUtils.isNotBlank(readBehaviorJson)) {
            ReadBehaviorDto readBehaviorDto = JSON.parseObject(readBehaviorJson, ReadBehaviorDto.class);
            dto.setCount((short) (readBehaviorDto.getCount() + dto.getCount()));
        }
        cacheService.hPut(BehaviorConstants.READ_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString(), JSON.toJSONString(dto));

        UpdateArticleMess updateArticleMess = new UpdateArticleMess();
        updateArticleMess.setArticleId(dto.getArticleId());
        updateArticleMess.setType(UpdateArticleMess.UpdateArticleType.VIEWS);
        updateArticleMess.setAdd(1);

        //发送消息，数据聚合
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC,JSON.toJSONString(updateArticleMess));
        //结果返回
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
