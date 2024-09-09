package com.heima.model.dto.comment;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class ArticleCommentDto  extends PageRequestDto {
    private String beginDate;
    private String endDate;
    private Integer wmUserId;
}
