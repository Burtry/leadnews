package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.article.ArticleDto;
import com.heima.model.dto.article.ArticleHomeDto;
import com.heima.model.dto.article.ArticleInfoDto;
import com.heima.model.dto.comment.ArticleCommentDto;
import com.heima.model.mess.ArticleVisitStreamMess;
import com.heima.model.pojo.article.ApArticle;


public interface ApArticleService extends IService<ApArticle> {

    /**
     * 根据参数加载文章列表
     * @param loadType 1为加载更多  2为加载最新
     * @param dto
     * @return
     */
    ResponseResult load(Short loadType, ArticleHomeDto dto);


    /**
     * 加载文章列表
     * @param dto
     * @param type  1 加载更多   2 加载最新
     * @param firstPage  true  是首页  flase 非首页
     * @return
     */
    ResponseResult load2(ArticleHomeDto dto,Short type,boolean firstPage);

    /**
     * 保存文章
     * @param articleDto
     */
    ResponseResult saveArticle(ArticleDto articleDto);

    /**
     * 加载文章行为-数据回显
     * @param articleInfoDto
     * @return
     */
    ResponseResult loadArticleBehavior(ArticleInfoDto articleInfoDto);


    /**
     * 更新文章分数
     * @param mess
     */
    void updateScore(ArticleVisitStreamMess mess);

    /**
     * 查询文章的评论列表
     * @param dto
     * @return
     */
    PageResponseResult findNewsComments(ArticleCommentDto dto);
}
