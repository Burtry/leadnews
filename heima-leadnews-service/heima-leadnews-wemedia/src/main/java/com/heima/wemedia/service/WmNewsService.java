package com.heima.wemedia.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.wemedia.NewsAutoPageReqDTO;
import com.heima.model.dto.wemedia.WmNewsDto;
import com.heima.model.dto.wemedia.WmNewsPageReqDto;
import com.heima.model.pojo.wemedia.WmNews;
import org.springframework.web.bind.annotation.RequestBody;

public interface WmNewsService extends IService<WmNews> {

    /**
     * 查询文章
     * @param dto
     * @return
     */
    ResponseResult findAll(WmNewsPageReqDto dto);

    ResponseResult suubmit(WmNewsDto dto);

    ResponseResult downOrUp(@RequestBody WmNewsDto dto);

    /**
     * 分页查询文章列表
     * @param newsAutoPageReqDTO
     * @return
     */
    ResponseResult getList(NewsAutoPageReqDTO newsAutoPageReqDTO);

    /**
     * 根据id查询文章详情
     * @param id
     * @return
     */
    ResponseResult getNewsById(Integer id);
}