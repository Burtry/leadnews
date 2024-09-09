package com.heima.model.dto.comment;
 
import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;
 
@Data
public class CommentManageDto extends PageRequestDto {
 
    /**
     * 文章id
     */
    private Long articleId;
}