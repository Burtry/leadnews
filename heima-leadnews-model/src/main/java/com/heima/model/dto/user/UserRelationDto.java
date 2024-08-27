package com.heima.model.dto.user;

import lombok.Data;

@Data
public class UserRelationDto {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 作者id
     */
    private Integer authorId;

    /**
     * 操作 0 关注/ 1 取消
     */
    private Short operation;
}
