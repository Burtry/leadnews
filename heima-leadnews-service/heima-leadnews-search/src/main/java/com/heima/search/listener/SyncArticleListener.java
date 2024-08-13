package com.heima.search.listener;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.vo.search.SearchArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class SyncArticleListener {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @KafkaListener(topics = ArticleConstants.ARTICLE_ES_SYNC_TOPIC)
    public void onMessage(String message) {

        if (StringUtils.isNotBlank(message)) {

            log.info("接收的消息为:{}",message);
            SearchArticleVo searchArticleVo = JSON.parseObject(message, SearchArticleVo.class);

            IndexRequest indexRequest = new IndexRequest("app_info_article");

            indexRequest.id(searchArticleVo.getId().toString());
            indexRequest.source(message, XContentType.JSON);
            try {
                restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error("同步es error ");
                throw new RuntimeException(e);

            }
        }
    }
}
