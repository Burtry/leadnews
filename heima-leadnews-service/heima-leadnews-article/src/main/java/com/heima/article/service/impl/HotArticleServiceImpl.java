package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.HotArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.pojo.article.ApArticle;
import com.heima.model.pojo.article.ApArticleContent;
import com.heima.model.pojo.wemedia.WmChannel;
import com.heima.model.vo.article.HotArticleVO;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class HotArticleServiceImpl implements HotArticleService {

    @Autowired
    private ApArticleMapper articleMapper;

    @Autowired
    private IWemediaClient wemediaClient;

    @Autowired
    private CacheService cacheService;

    @Override
    public void computeHotArticle() {

        //获取前五天的文章
        List<ApArticle> articleListByLast5Days = articleMapper.findArticleListByLast5Days(DateTime.now().minusDays(5).toDate());

        //计算文章分值
        List<HotArticleVO> articleVOList = computeHotArticle(articleListByLast5Days);

        //为每个频道设置30条分值较高的数据到redis
        cacheTagToRedis(articleVOList);

    }

    private void cacheTagToRedis(List<HotArticleVO> articleVOList) {
        ResponseResult responseResult = wemediaClient.getChannel();
        if (responseResult.getCode().equals(200)) {
            String channelJSON = JSON.toJSONString(responseResult.getData());

            List<WmChannel> wmChannelList = JSON.parseArray(channelJSON, WmChannel.class);

            if (wmChannelList != null && !wmChannelList.isEmpty()) {
                for (WmChannel wmChannel : wmChannelList) {
                    //获取频道下的文章
                    List<HotArticleVO> hotArticleVOs = articleVOList.stream().filter(hotArticleVO -> hotArticleVO.getChannelId().equals(wmChannel.getId())).collect(Collectors.toList());

                    //给文章进行排序，取30条分值较高的存入redis
                    sortAndCache(hotArticleVOs, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + wmChannel.getId());
                }
            }
        }

        //设置推荐数据
        //给文章进行排序，取30条分值较高的存入redis
        sortAndCache(articleVOList, ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);
    }

    /**
     * 排序并且缓存数据
     * @param articleVOList
     * @param key
     */
    private void sortAndCache(List<HotArticleVO> articleVOList, String key) {
        articleVOList = articleVOList.stream().sorted(Comparator.comparing(HotArticleVO::getScore).reversed()).collect(Collectors.toList());

        if (articleVOList.size() > 30) {
            articleVOList = articleVOList.subList(0, 30);
        }

        cacheService.set(key, JSON.toJSONString(articleVOList));
    }

    /**
     * 计算文章分值
     * @param articleListByLast5Days
     * @return
     */
    private List<HotArticleVO> computeHotArticle(List<ApArticle> articleListByLast5Days) {
        List<HotArticleVO> articleVOList = new ArrayList<>();
        if (articleListByLast5Days != null && !articleListByLast5Days.isEmpty()) {
            for (ApArticle article : articleListByLast5Days) {
                HotArticleVO hotArticleVO = new HotArticleVO();
                BeanUtils.copyProperties(article,hotArticleVO);

                //设置文章分值
                Integer score = computeScore(article);
                hotArticleVO.setScore(score);
                articleVOList.add(hotArticleVO);
            }
        }
        return articleVOList;

    }

    /**
     * 计算文章分值
     * @param apArticle
     * @return
     */
    private Integer computeScore(ApArticle apArticle) {
            Integer scere = 0;
            if(apArticle.getLikes() != null){
                scere += apArticle.getLikes() * ArticleConstants.HOT_ARTICLE_LIKE_WEIGHT;
            }
            if(apArticle.getViews() != null){
                scere += apArticle.getViews();
            }
            if(apArticle.getComment() != null){
                scere += apArticle.getComment() * ArticleConstants.HOT_ARTICLE_COMMENT_WEIGHT;
            }
            if(apArticle.getCollection() != null){
                scere += apArticle.getCollection() * ArticleConstants.HOT_ARTICLE_COLLECTION_WEIGHT;
            }
            return scere;
    }


}
