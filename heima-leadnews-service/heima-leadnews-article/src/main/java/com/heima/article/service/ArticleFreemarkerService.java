package com.heima.article.service;

import com.heima.model.pojo.article.ApArticle;

public interface ArticleFreemarkerService {

    /**
     * 生成静态文件上传Minio中
     * @param apArticle
     * @param content
     */
    void buildArticleToMinio(ApArticle apArticle, String content);
}
