package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.dto.AdminLoginDTO;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.pojo.AdUser;
import com.heima.admin.service.AdminLoginService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class AdminLoginServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdminLoginService {



    @Override
    public ResponseResult login(AdminLoginDTO adminLoginDTO) {
        if (StringUtils.isBlank(adminLoginDTO.getName()) || StringUtils.isBlank(adminLoginDTO.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"请输入正确的name或password");
        }
        //获取用户
        AdUser adUser = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, adminLoginDTO.getName()));

        String md5Pwd = DigestUtils.md5DigestAsHex((adminLoginDTO.getPassword() + adUser.getSalt()).getBytes());

        if (!adUser.getPassword().equals(md5Pwd)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }

        String token = AppJwtUtil.getToken(adUser.getId().longValue());
        Map<String,Object> map = new HashMap<>();

        adUser.setPassword("");
        adUser.setSalt("");
        map.put("adUser",adUser);
        map.put("token",token);

        return ResponseResult.okResult(map);
    }
}
