package com.heima.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.pojo.user.ApUserRealname;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAuthMapper extends BaseMapper<ApUserRealname> {
}
