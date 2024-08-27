package com.heima.model.dto.user;

import com.heima.model.common.annotation.IdEncrypt;
import lombok.Data;

@Data
public class UserRelationDto {

    /**
     * 文章id
     */
    @IdEncrypt
    private Integer articleId;

    /**
     * 作者id
     */
    @IdEncrypt
    private Integer authorId;

    /**
     * 操作 0 关注/ 1 取消
     */
    private Short operation;
}
