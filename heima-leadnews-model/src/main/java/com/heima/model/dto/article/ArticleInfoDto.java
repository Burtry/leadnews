package com.heima.model.dto.article;

import com.heima.model.common.annotation.IdEncrypt;
import lombok.Data;

@Data
public class ArticleInfoDto {

    @IdEncrypt
    private Long articleId;

    private Integer authorId;
}
