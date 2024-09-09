package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.comment.CommentConfigDto;
import com.heima.model.pojo.article.ApArticleConfig;

import java.util.Map;

public interface ApArticleConfigService extends IService<ApArticleConfig> {

    /**
     * 修改文章配置
     * @param map
     */
    void updateByMap(Map map);

    /**
     * 更新文章的评论设置->打开或关闭评论
     * @param dto
     * @return
     */
    ResponseResult updateCommentStatus(CommentConfigDto dto);

}
