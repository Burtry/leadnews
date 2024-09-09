package com.heima.apis.user;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.pojo.user.ApUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("leadnews-user")
public interface IUserClient {

    @PostMapping("/api/v1/user/save")
    ResponseResult save(@RequestBody ApUser user);

    @GetMapping("/api/v1/user/findUserById/{id}")
    ApUser findUserById(@PathVariable("id") Integer id);
}
