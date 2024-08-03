package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.wemedia.WmNewsDto;
import com.heima.model.dto.wemedia.WmNewsPageReqDto;
import com.heima.model.pojo.wemedia.WmNews;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 查询文章
     * @param dto
     * @return
     */
    ResponseResult findAll(WmNewsPageReqDto dto);

    ResponseResult suubmit(WmNewsDto dto);
}