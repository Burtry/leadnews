package com.heima.model.dto.article;

import com.heima.model.pojo.article.ApArticle;
import lombok.Data;

@Data
public class ArticleDto extends ApArticle {
    /**
     * 文章内容
     */
    private String content;
}
