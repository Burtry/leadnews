package com.heima.model.pojo.article;

import lombok.Data;

import java.util.Date;

@Data
public class APCollection {
    private Long id;

    /**
     * 文章id
     */
    private Long entryId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 类型 0 文章 1 动态
     */
    private Short type;

    /**
     * 收藏时间
     */
    private Date collectionTime;

    /**
     * 发布时间
     */
    private  Date publishedTime;

}
