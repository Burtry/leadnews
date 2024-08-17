package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.dtos.UserAuthPageRequestDTO;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.pojo.user.ApUserRealname;
import com.heima.user.mapper.UserAuthMapper;
import com.heima.user.service.UserAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class UserAuthServiceImpl extends ServiceImpl<UserAuthMapper, ApUserRealname> implements UserAuthService {

    @Override
    public ResponseResult getList(UserAuthPageRequestDTO userAuthPageRequestDTO) {

        //检查参数
        userAuthPageRequestDTO.checkParam();

        //构建分页
        IPage page = new Page(userAuthPageRequestDTO.getPage(), userAuthPageRequestDTO.getSize());

        //构建分页条件

        QueryWrapper<ApUserRealname> apUserRealnameQueryWrapper = new QueryWrapper<>();

        //根据审核状态条件查询
        if (userAuthPageRequestDTO.getStatus() != null) {
            apUserRealnameQueryWrapper.eq("status",userAuthPageRequestDTO.getStatus());
        }

        //分页查询
        page(page,apUserRealnameQueryWrapper);


        //封装结果
        ResponseResult responseResult = new PageResponseResult(userAuthPageRequestDTO.getPage(),userAuthPageRequestDTO.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());

        return responseResult;
    }

    @Override
    public ResponseResult authPass(UserAuthPageRequestDTO userAuthPageRequestDTO) {

        if (userAuthPageRequestDTO == null || userAuthPageRequestDTO.getId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        ApUserRealname apUserRealname = getById(userAuthPageRequestDTO.getId());

        if (apUserRealname == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        apUserRealname.setStatus((short) 9); //设置为审核通过的状态
        //更新
        updateById(apUserRealname);
        //获取列表

        return ResponseResult.okResult(200,"操作成功");

    }

    @Override
    public ResponseResult authFail(UserAuthPageRequestDTO userAuthPageRequestDTO) {

        if (userAuthPageRequestDTO == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUserRealname apUserRealname = getById(userAuthPageRequestDTO.getId());
        if (apUserRealname == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //设置为审核失败状态
        apUserRealname.setStatus((short) 2);
        apUserRealname.setReason(userAuthPageRequestDTO.getMsg());
        updateById(apUserRealname);

        return ResponseResult.okResult(200,"操作成功");
    }
}
