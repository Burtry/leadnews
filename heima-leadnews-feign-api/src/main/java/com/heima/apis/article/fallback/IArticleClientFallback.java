package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.article.ArticleDto;
import org.springframework.stereotype.Component;

@Component
public class IArticleClientFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto articleDto) {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"获取数据失败");
    }

    @Override
    public ResponseResult findArticleConfigByArticleId(Long articleId) {
        return null;
    }
}
