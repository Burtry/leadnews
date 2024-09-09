package com.heima.apis.article;

import com.heima.apis.article.fallback.IArticleClientFallback;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.article.ArticleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-article",fallback = IArticleClientFallback.class)
public interface IArticleClient{

    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(@RequestBody ArticleDto articleDto);


    /**
     * 查询文章配置信息
     * @param articleId
     * @return
     */
    @GetMapping("/api/v1/article/findArticleConfigByArticleId/{articleId}")
    ResponseResult findArticleConfigByArticleId(@PathVariable("articleId") Long articleId);



}
