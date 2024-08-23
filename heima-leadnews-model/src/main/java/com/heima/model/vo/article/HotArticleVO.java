package com.heima.model.vo.article;

import com.heima.model.pojo.article.ApArticle;
import lombok.Data;

@Data
public class HotArticleVO extends ApArticle {

    /**
     * 文章分值
     */
    private Integer score;
}
