package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.AdminChannelPageRequestDTO;
import com.heima.model.common.dtos.AdminLoginDTO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.wemedia.AdChannelDTO;
import com.heima.model.pojo.wemedia.WmChannel;

public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询所有频道
     * @return
     */
    ResponseResult findAll();


    /**
     * 管理端获取频道列表
     * @param adminChannelPageRequestDTO
     * @return
     */
    ResponseResult getList(AdminChannelPageRequestDTO adminChannelPageRequestDTO);

    /**
     * 添加频道
     * @param adChannelDTO
     * @return
     */
    ResponseResult saveChannel(AdChannelDTO adChannelDTO);

    /**
     * 删除频道
     * @param id
     * @return
     */
    ResponseResult delete(Integer id);

    /**
     * 更新频道
     * @param adChannelDTO
     * @return
     */
    ResponseResult updateChannel(AdChannelDTO adChannelDTO);
}