package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.search.UserSearchDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

public interface ArticleSearchService {
    /**
     * es文章分页检索
     * @param userSearchDto
     * @return
     */
    ResponseResult search(UserSearchDto userSearchDto) throws IOException;
}
