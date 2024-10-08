package com.heima.model.dto.behavior;

import com.heima.model.common.annotation.IdEncrypt;
import lombok.Data;

@Data
public class LikesBehaviorDto {

    @IdEncrypt
    private Long articleId;

    private Short type;
    /**
     * 0点赞
     * 1取消点赞
     */
    private Short operation;
}
