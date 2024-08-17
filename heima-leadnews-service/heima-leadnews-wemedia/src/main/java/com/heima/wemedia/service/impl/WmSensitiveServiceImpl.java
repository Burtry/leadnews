package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageRequestAndNameDTO;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.pojo.wemedia.WmSensitive;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {


    @Override
    public ResponseResult getList(PageRequestAndNameDTO pageRequestAndNameDTO) {

        //检查参数
        pageRequestAndNameDTO.checkParam();

        IPage page = new Page(pageRequestAndNameDTO.getPage(), pageRequestAndNameDTO.getSize());

        //构建查询条件
        QueryWrapper<WmSensitive> wmSensitiveQueryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(pageRequestAndNameDTO.getName())) {
            wmSensitiveQueryWrapper.like("name",pageRequestAndNameDTO.getName());
        }

        //根据创建时间倒序排序
        wmSensitiveQueryWrapper.orderByDesc("created_time");

        //分页查询
        page(page,wmSensitiveQueryWrapper);

        ResponseResult responseResult = new PageResponseResult(pageRequestAndNameDTO.getPage(), pageRequestAndNameDTO.getSize(), (int) page.getTotal());

        responseResult.setData(page.getRecords());

        return responseResult;
    }

    @Override
    public ResponseResult saveSensitive(WmSensitive wmSensitive) {
        wmSensitive.setCreatedTime(new Date());
        save(wmSensitive);
        return ResponseResult.okResult(200,"新增成功");
    }

    @Override
    public ResponseResult deleteById(Integer id) {
        if (id == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        removeById(id);
        return ResponseResult.okResult(200,"删除成功");
    }

    @Override
    public ResponseResult updateSensiticve(WmSensitive wmSensitive) {
        if (wmSensitive == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (wmSensitive.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        update(wmSensitive, Wrappers.<WmSensitive>lambdaUpdate().eq(WmSensitive::getId,wmSensitive.getId()));

        return ResponseResult.okResult(200,"更新成功");
    }
}
