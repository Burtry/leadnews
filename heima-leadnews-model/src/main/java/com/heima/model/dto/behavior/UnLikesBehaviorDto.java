package com.heima.model.dto.behavior;

import com.heima.model.common.annotation.IdEncrypt;
import lombok.Data;

@Data
public class UnLikesBehaviorDto {

    @IdEncrypt
    private Long articleId;

    /**
     * 0 不喜欢
     * 1取消不喜欢
     */
    private Short type;

}
