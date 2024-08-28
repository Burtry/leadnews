package com.heima.model.dto.behavior;

import lombok.Data;

@Data
public class LikesBehaviorDto {
    private Long articleId;

    private Short type;
    /**
     * 0点赞
     * 1取消点赞
     */
    private Short operation;
}
