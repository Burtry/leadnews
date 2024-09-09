package com.heima.article.feign;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleConfigService;
import com.heima.article.service.ApArticleService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.article.ArticleDto;
import com.heima.model.dto.comment.ArticleCommentDto;
import com.heima.model.dto.comment.CommentConfigDto;
import com.heima.model.pojo.article.ApArticleConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService articleService;

    @Autowired
    private ApArticleConfigService apArticleConfigService;

    @PostMapping("/api/v1/article/save")
    @Override
    public ResponseResult saveArticle(@RequestBody ArticleDto articleDto) {
        return articleService.saveArticle(articleDto);
    }

    @GetMapping("/api/v1/article/findArticleConfigByArticleId/{articleId}")
    @Override
    public ResponseResult findArticleConfigByArticleId(@PathVariable("articleId") Long articleId) {
        ApArticleConfig apArticleConfig = apArticleConfigService.getOne(Wrappers.<ApArticleConfig>lambdaQuery().eq(ApArticleConfig::getArticleId, articleId));
        return ResponseResult.okResult(apArticleConfig);
    }



    @PostMapping("/api/v1/article/findNewsComments")
    @Override
    public PageResponseResult findNewsComments(@RequestBody ArticleCommentDto dto) {
        return articleService.findNewsComments(dto);
    }



    @PostMapping("/api/v1/article/updateCommentStatus")
    @Override
    public ResponseResult updateCommentStatus(@RequestBody CommentConfigDto dto) {
        return apArticleConfigService.updateCommentStatus(dto);

    }


}