package com.heima.article.stream;

import com.alibaba.fastjson.JSON;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.mess.UpdateArticleMess;
import com.sun.xml.internal.rngom.digested.DValuePattern;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class HotArticleStreamHandler {
    @Bean
    public KStream<String, String> kStream(StreamsBuilder streamsBuilder) {
        //接收消息
        KStream<String, String> stream = streamsBuilder.stream(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC);

        //聚合处理
        stream.map((key, value) -> {
            UpdateArticleMess updateArticleMess = JSON.parseObject(value, UpdateArticleMess.class);
            return new KeyValue<>(updateArticleMess.getArticleId().toString(),updateArticleMess.getType().name() + ":" + updateArticleMess.getAdd());
        })
                //按照文章id进行聚合
                .groupBy((key,value) -> key)
                //时间窗口
                .windowedBy(TimeWindows.of(Duration.ofSeconds(10)))
                /**
                 * 自行的聚合计算逻辑
                 */
                .aggregate(new Initializer<String>() {
                    /**
                     * 初始方法，返回值是消息的value
                     * @return
                     */
                    @Override
                    public String apply() {
                        return "COLLECTION:0,COMMENT:0,LIKES:0,VIEWS:0";
                    }
                    /**
                     * 真正的聚合操作,返回的是消息的value
                     */
                }, new Aggregator<String, String, String>() {
                    @Override
                    public String apply(String key, String value, String aggValue) {
                        if (StringUtils.isBlank(value)) {
                            return aggValue;
                        }
                        int collect = 0,comment=0,likes=0,views=0;
                        String[] arrAgg = aggValue.split(",");
                        for (String agg : arrAgg) {
                            String[] split = agg.split(":");
                            /**
                             * 获得初始值，也是时间窗口内计算之后的值
                             */
                            switch (UpdateArticleMess.UpdateArticleType.valueOf(split[0])) {
                                case LIKES:
                                    likes = Integer.parseInt(split[1]);
                                    break;
                                case COMMENT:
                                    comment = Integer.parseInt(split[1]);
                                    break;
                                case COLLECTION:
                                    collect = Integer.parseInt(split[1]);
                                    break;
                                case VIEWS:
                                    views = Integer.parseInt(split[1]);
                                    break;
                            }
                        }
                        /**
                         * 获得到的数组下标0为操作名 下标1为更新值( 1/-1 )
                         */
                        String[] valAry = value.split(":");
                        switch (UpdateArticleMess.UpdateArticleType.valueOf(valAry[0])) {
                            case LIKES:
                                likes += Integer.parseInt(valAry[1]);
                                break;
                            case COMMENT:
                                comment += Integer.parseInt(valAry[1]);
                                break;
                            case COLLECTION:
                                collect += Integer.parseInt(valAry[1]);
                                break;
                            case VIEWS:
                                views += Integer.parseInt(valAry[1]);
                                break;
                        }

                        String formatStr = String.format("COLLECTION:%d,COMMENT:%d,LIKES:%d,VIEWS:%d", collect, comment, likes, views);
                        System.out.println("articleId" + key);

                        System.out.println("当前时间窗口内的处理结果:" + formatStr);

                        return formatStr;
                    }
                }, Materialized.as("hot-article-stream-count-001"))
                .toStream()
                .map((key,value) -> {
                    //处理key和value
                    return new KeyValue<>(key.key().toString(),formatObj(key.key().toString(),value));
                })
                //发送消息
                .to(HotArticleConstants.HOT_ARTICLE_INCR_HANDLE_TOPIC);

        return stream;
    }

    /**
     * 格式化消息value数据
     * @param articleId
     * @param value
     * @return
     */
    private String formatObj(String articleId, String value) {

        ArticleVisitStreamMess mess = new ArticleVisitStreamMess();
        mess.setArticleId(Long.valueOf(articleId));

        //"COLLECTION:0,COMMENT:0,LIKES:0,VIEWS:0"
        String[] valArr = value.split(",");

        for (String val : valArr) {

            String[] split = val.split(":");
            switch (UpdateArticleMess.UpdateArticleType.valueOf(split[0])) {
                case LIKES:
                    mess.setLike(Integer.parseInt(split[1]));
                    break;
                case COMMENT:
                    mess.setComment(Integer.parseInt(split[1]));
                    break;
                case COLLECTION:
                    mess.setCollect(Integer.parseInt(split[1]));
                    break;
                case VIEWS:
                    mess.setView(Integer.parseInt(split[1]));
                    break;
            }

        }
        log.info("聚合操作处理后结果为:" + JSON.toJSONString(mess));
        return JSON.toJSONString(mess);
    }
}
