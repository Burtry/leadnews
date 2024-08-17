package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.AdminChannelPageRequestDTO;
import com.heima.model.common.dtos.AdminLoginDTO;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.wemedia.AdChannelDTO;
import com.heima.model.pojo.wemedia.WmChannel;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
@Slf4j
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {


    /**
     * 查询所有频道
     * @return
     */
    @Override
    public ResponseResult findAll() {
        return ResponseResult.okResult(list());
    }

    @Override
    public ResponseResult getList(AdminChannelPageRequestDTO adminChannelPageRequestDTO) {

        //检查参数
        adminChannelPageRequestDTO.checkParam();

        //分页查询
        IPage page = new Page(adminChannelPageRequestDTO.getPage(), adminChannelPageRequestDTO.getSize());

        //构建条件
        QueryWrapper<WmChannel> wmChannelQueryWrapper = new QueryWrapper<>();

        //根据如果存在name，模糊查询
        if (StringUtils.isNotBlank(adminChannelPageRequestDTO.getName())) {
            wmChannelQueryWrapper.like("name",adminChannelPageRequestDTO.getName());
        }
        page(page,wmChannelQueryWrapper);

        ResponseResult responseResult = new PageResponseResult(adminChannelPageRequestDTO.getPage(),adminChannelPageRequestDTO.getSize(),(int)page.getTotal());

        responseResult.setData(page.getRecords());
        return responseResult;

    }

    @Override
    public ResponseResult saveChannel(AdChannelDTO adChannelDTO) {
        WmChannel wmChannel = new WmChannel();
        BeanUtils.copyProperties(adChannelDTO,wmChannel);
        wmChannel.setCreatedTime(new Date());
        save(wmChannel);
        return ResponseResult.okResult(200,"添加成功");
    }

    @Override
    public ResponseResult delete(Integer id) {
        removeById(id);
        return ResponseResult.okResult(200,"删除成功!!!!!!");
    }
}