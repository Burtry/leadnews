package com.heima.apis.article;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.article.ArticleDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("leadnews-article")
public interface IArticleClient{

    @PostMapping("/api/v1/article/save")
    ResponseResult saveArticle(@RequestBody ArticleDto articleDto);
}
