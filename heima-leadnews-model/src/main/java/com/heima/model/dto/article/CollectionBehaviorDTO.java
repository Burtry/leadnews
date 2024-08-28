package com.heima.model.dto.article;

import com.heima.model.common.annotation.IdEncrypt;
import lombok.Data;

import java.util.Date;

@Data
public class CollectionBehaviorDTO {

    /**
     * 文章id
     */
    @IdEncrypt
    private Long entryId;

    /**
     * 0收藏 1取消收藏
     */
    private Short operation;

    /**
     * 0 文章 1 动态
     */
    private Short type;

    /**
     * 发布时间
     */
    private Date publishedTime;
}
