package com.heima.article.controller.v1;

import com.heima.article.service.ApArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.article.ArticleHomeDto;
import com.heima.model.dto.article.ArticleInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/article")
public class ArticleHomeController {

    @Autowired
    private ApArticleService articleService;

    @PostMapping("/load")
    public ResponseResult load(@RequestBody ArticleHomeDto dto) {

        //return articleService.load(ArticleConstants.LOADTYPE_LOAD_MORE,dto);
        return articleService.load2(dto,ArticleConstants.LOADTYPE_LOAD_MORE,true);
    }


    @PostMapping("/loadmore")
    public ResponseResult loadMore(@RequestBody ArticleHomeDto dto) {

        return articleService.load(ArticleConstants.LOADTYPE_LOAD_MORE,dto);

    }

    @PostMapping("/loadnew")
    public ResponseResult loadNew(@RequestBody ArticleHomeDto dto) {
        return articleService.load(ArticleConstants.LOADTYPE_LOAD_NEW,dto);
    }


    @PostMapping("/load_article_behavior")
    public ResponseResult loadArticleBehavior(@RequestBody ArticleInfoDto articleInfoDto) {
        return articleService.loadArticleBehavior(articleInfoDto);
    }
}
