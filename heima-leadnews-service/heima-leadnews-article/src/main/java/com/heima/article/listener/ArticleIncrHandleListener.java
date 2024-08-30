package com.heima.article.listener;

import com.heima.common.constants.HotArticleConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * 接收kafka的消息
 */
@Component
@Slf4j
public class ArticleIncrHandleListener {

    @KafkaListener(topics = HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC)
    public void onMessage(String mess) {
        if (StringUtils.isNotBlank(mess)) {
            System.out.println(mess);
        }
    }
}
