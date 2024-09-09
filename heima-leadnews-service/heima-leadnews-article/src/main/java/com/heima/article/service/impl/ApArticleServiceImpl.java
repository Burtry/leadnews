package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.article.ArticleDto;
import com.heima.model.dto.article.ArticleHomeDto;
import com.heima.model.dto.article.ArticleInfoDto;
import com.heima.model.dto.comment.ArticleCommentDto;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.pojo.article.ApArticle;
import com.heima.model.pojo.article.ApArticleConfig;
import com.heima.model.pojo.article.ApArticleContent;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.vo.article.HotArticleVO;
import com.heima.model.vo.comment.ArticleCommentVo;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.nntp.Article;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    private final static short MAX_PAGE_SIZE = 50;

    @Autowired
    private ApArticleMapper articleMapper;

    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;

    @Autowired
    private CacheService cacheService;

    @Override
    public ResponseResult load(Short loadType, ArticleHomeDto dto) {
        //参数校验
        //1.校验参数
        Integer size = dto.getSize();
        if(size == null || size == 0){
            size = 10;
        }
        size = Math.min(size,MAX_PAGE_SIZE);
        dto.setSize(size);

        //类型参数检验
        if(!loadType.equals(ArticleConstants.LOADTYPE_LOAD_MORE)&&!loadType.equals(ArticleConstants.LOADTYPE_LOAD_NEW)){
            loadType = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        //文章频道校验
        if(StringUtils.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if(dto.getMaxBehotTime() == null) dto.setMaxBehotTime(new Date());
        if(dto.getMinBehotTime() == null) dto.setMinBehotTime(new Date());

        List<ApArticle> articleList = articleMapper.loadArticleList(dto, loadType);
        return ResponseResult.okResult(articleList);

    }

    @Override
    public ResponseResult load2(ArticleHomeDto dto, Short type, boolean firstPage) {
        if (firstPage) {
            String jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if(StringUtils.isNotBlank(jsonStr)){
                List<HotArticleVO> hotArticleVoList = JSON.parseArray(jsonStr, HotArticleVO.class);
                ResponseResult responseResult = ResponseResult.okResult(hotArticleVoList);
                return responseResult;
            }
        }

        return load(type,dto);
    }

    @Override
    public ResponseResult saveArticle(ArticleDto dto) {

        //try {
        //    Thread.sleep(3000);
        //} catch (InterruptedException e) {
        //    throw new RuntimeException(e);
        //}

        //1.检查参数
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto,apArticle);

        //2.判断是否存在id
        if(dto.getId() == null){
            //2.1 不存在id  保存  文章  文章配置  文章内容

            //保存文章
            save(apArticle);

            //保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            //保存 文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);

        }else {
            //2.2 存在id   修改  文章  文章内容

            //修改  文章
            updateById(apArticle);

            //修改文章内容
            ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        //异步调用，生成静态文件上传minio中
        articleFreemarkerService.buildArticleToMinio(apArticle,dto.getContent());



        //3.结果返回  文章的id
        return ResponseResult.okResult(apArticle.getId());
    }


    @Override
    public ResponseResult loadArticleBehavior(ArticleInfoDto dto) {

        if (dto == null || dto.getArticleId() == null || dto.getAuthorId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        boolean isFollow = false, isLike = false, isUnlike = false, isCollection = false;

        // 3.1 喜欢行为
        String likeBehaviorJson = (String) cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
        if(StringUtils.isNotBlank(likeBehaviorJson)){
            isLike = true;
        }
        // 3.2 不喜欢的行为
        String unLikeBehaviorJson = (String) cacheService.hGet(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId().toString(), user.getId().toString());
        if(StringUtils.isNotBlank(unLikeBehaviorJson)){
            isUnlike = true;
        }
        // 3.3 是否收藏
        String collctionJson = (String) cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR+user.getId(),dto.getArticleId().toString());
        if(StringUtils.isNotBlank(collctionJson)){
            isCollection = true;
        }

        // 3.4 是否关注
        Double score = cacheService.zScore(BehaviorConstants.APUSER_FOLLOW_RELATION + user.getId(), dto.getAuthorId().toString());
        if(score != null){
            isFollow = true;
        }

        Map<String,Boolean> result = new HashMap<>();
        result.put("islike", isLike);
        result.put("isunlike", isUnlike);
        result.put("iscollection", isCollection);
        result.put("isfollow", isFollow);

        return ResponseResult.okResult(result);
    }

    @Override
    public void updateScore(ArticleVisitStreamMess mess) {
        //更新文章行为数据
        ApArticle article = updateArticle(mess);

        //计算score
        Integer score = computeScore(article);

        score = score * 3;

        //替换当前文章对应频道的热点数据

        String articleListStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + article.getChannelId());

        if (StringUtils.isNotBlank(articleListStr)) {
            List<HotArticleVO> articleVOList = JSON.parseArray(articleListStr, HotArticleVO.class);

            //flag为标记，标记是否在缓存articleVoList中找到该数据,找到了只更新分数,没找到就替换或新增
            boolean flag = true;
            //缓存中存在该数据,只更新分值

            for (HotArticleVO hotArticleVO : articleVOList) {
                if (hotArticleVO.getId().equals(article.getId())) {

                    hotArticleVO.setScore(score);
                    flag = false;   //找到了设置为false,就不执行下述新增和替换操作
                    break;
                }
            }

            if (flag) {
                //缓存中不存在数据，查询分值最小的一条数据，进行分值比较，较小的替换掉 ，如果list小于30条，直接添加
                if (articleVOList.size() >= 30) {
                    //替换操作
                    articleVOList = articleVOList.stream().sorted(Comparator.comparing(HotArticleVO::getScore).reversed()).collect(Collectors.toList());

                    //分值最小的数据
                    HotArticleVO lastHot = articleVOList.get(articleVOList.size() - 1);
                    if (lastHot.getScore() < score) {
                        articleVOList.remove(lastHot);
                        HotArticleVO hotArticleVO = new HotArticleVO();

                        BeanUtils.copyProperties(article,hotArticleVO);
                        hotArticleVO.setScore(score);
                        articleVOList.add(hotArticleVO);
                    }
                } else {
                    //直接添加
                    HotArticleVO hotArticleVO = new HotArticleVO();
                    BeanUtils.copyProperties(article,hotArticleVO);
                    hotArticleVO.setScore(score);
                    articleVOList.add(hotArticleVO);
                }
            }

            //缓存到redis中
            articleVOList = articleVOList.stream().sorted(Comparator.comparing(HotArticleVO::getScore).reversed()).collect(Collectors.toList());
            cacheService.set(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + article.getChannelId(),JSON.toJSONString(articleVOList));
        }

        //替换推荐对应的热点数据

        String articleListAllStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG);

        if (StringUtils.isNotBlank(articleListAllStr)) {
            List<HotArticleVO> articleVOList = JSON.parseArray(articleListAllStr, HotArticleVO.class);

            //flag为标记，标记是否在缓存articleVoList中找到该数据,找到了只更新分数,没找到就替换或新增
            boolean flag = true;
            //缓存中存在该数据,只更新分值

            for (HotArticleVO hotArticleVO : articleVOList) {
                if (hotArticleVO.getId().equals(article.getId())) {

                    hotArticleVO.setScore(score);
                    flag = false;   //找到了设置为false,就不执行下述新增和替换操作
                    break;
                }
            }

            if (flag) {
                //缓存中不存在数据，查询分值最小的一条数据，进行分值比较，较小的替换掉 ，如果list小于30条，直接添加
                if (articleVOList.size() >= 30) {
                    //替换操作
                    articleVOList = articleVOList.stream().sorted(Comparator.comparing(HotArticleVO::getScore).reversed()).collect(Collectors.toList());

                    //分值最小的数据
                    HotArticleVO lastHot = articleVOList.get(articleVOList.size() - 1);
                    if (lastHot.getScore() < score) {
                        articleVOList.remove(lastHot);
                        HotArticleVO hotArticleVO = new HotArticleVO();

                        BeanUtils.copyProperties(article,hotArticleVO);
                        hotArticleVO.setScore(score);
                        articleVOList.add(hotArticleVO);
                    }
                } else {
                    //直接添加
                    HotArticleVO hotArticleVO = new HotArticleVO();
                    BeanUtils.copyProperties(article,hotArticleVO);
                    hotArticleVO.setScore(score);
                    articleVOList.add(hotArticleVO);
                }
            }

            //缓存到redis中
            articleVOList = articleVOList.stream().sorted(Comparator.comparing(HotArticleVO::getScore).reversed()).collect(Collectors.toList());
            cacheService.set(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + ArticleConstants.DEFAULT_TAG,JSON.toJSONString(articleVOList));
        }

    }

    /**
     * 更新行为数据
     * @param mess
     * @return
     */
    private ApArticle updateArticle(ArticleVisitStreamMess mess) {
        ApArticle apArticle = getById(mess.getArticleId());
        apArticle.setCollection(apArticle.getCollection() == null ? 0 : apArticle.getCollection() + mess.getCollect());
        apArticle.setComment(apArticle.getComment() == null ? 0 : apArticle.getComment() + mess.getComment());
        apArticle.setLikes(apArticle.getLikes() == null ? 0 : apArticle.getLikes() + mess.getLike());
        apArticle.setViews(apArticle.getViews() == null ? 0 : apArticle.getViews() + mess.getView());
        updateById(apArticle);
        return apArticle;
    }

    /**
     * 计算文章分值 当日的分值再 *3
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


    /**
     * 查询文章的评论统计
     * @param dto
     * @return
     */
    @Override
    public PageResponseResult findNewsComments(ArticleCommentDto dto) {
        // 1. 统计文章评论信息
        Integer currentPage = dto.getPage();
        dto.setPage((dto.getPage() - 1) * dto.getSize());
        List<ArticleCommentVo> list = articleMapper.findNewsComments(dto);
        int count = articleMapper.findNewsCommentsCount(dto);

        // 2. 构造结果返回
        PageResponseResult responseResult = new PageResponseResult(currentPage, dto.getSize(), count);
        responseResult.setData(list);
        return responseResult;
    }
}
