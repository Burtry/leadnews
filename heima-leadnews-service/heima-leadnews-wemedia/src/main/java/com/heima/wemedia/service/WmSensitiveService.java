package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.PageRequestAndNameDTO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.pojo.wemedia.WmChannel;
import com.heima.model.pojo.wemedia.WmSensitive;

public interface WmSensitiveService extends IService<WmSensitive> {

    /**
     * 分页获取敏感词列表
     * @param pageRequestAndNameDTO
     * @return
     */
    ResponseResult getList(PageRequestAndNameDTO pageRequestAndNameDTO);

    /**
     * 新增敏感词
     * @param wmSensitive
     * @return
     */
    ResponseResult saveSensitive(WmSensitive wmSensitive);

    /***
     * 删除敏感词
     * @param id
     * @return
     */
    ResponseResult deleteById(Integer id);

    /**
     * 更新敏感词
     * @param wmSensitive
     * @return
     */
    ResponseResult updateSensiticve(WmSensitive wmSensitive);
}
