package com.heima.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.pojo.user.ApUserFollow;
import org.apache.commons.net.nntp.Article;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserBehaviorMapper extends BaseMapper<ApUserFollow> {

}
