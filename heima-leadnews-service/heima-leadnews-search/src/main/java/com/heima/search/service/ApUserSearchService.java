package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;

public interface ApUserSearchService {

    /**
     * 保存用户的搜索记录
     * @param keyword
     * @param userId
     */
    void insert(String keyword, Integer userId);

    /**
     * 查询用户搜索历史记录
     * @return
     */
    ResponseResult findUserSearch();

}
