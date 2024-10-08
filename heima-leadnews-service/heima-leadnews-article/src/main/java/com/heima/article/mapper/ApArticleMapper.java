package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.heima.model.dto.article.ArticleHomeDto;
import com.heima.model.dto.comment.ArticleCommentDto;
import com.heima.model.pojo.article.ApArticle;
import com.heima.model.vo.comment.ArticleCommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    List<ApArticle> loadArticleList(@Param("dto") ArticleHomeDto dto, @Param("type") Short type);

    List<ApArticle> findArticleListByLast5Days(Date date);


    List<ArticleCommentVo> findNewsComments(ArticleCommentDto dto);

    int findNewsCommentsCount(ArticleCommentDto dto);
}