package com.heima.wemedia.feign;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.pojo.wemedia.WmUser;
import com.heima.wemedia.service.WmChannelService;
import com.heima.wemedia.service.WmSensitiveService;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class WemediaClient implements IWemediaClient {

    @Autowired
    private WmChannelService wmChannelService;

    @Autowired
    private WmUserService userService;

    @Autowired
    private WmSensitiveService wmSensitiveService;


    @GetMapping("/api/v1/channel/list")
    @Override
    public ResponseResult getChannel() {
        return wmChannelService.findAll();
    }

    @GetMapping("/api/c1/user/findByName/{name}")
    @Override
    public WmUser findWmUserByName(@PathVariable("name")String name) {
        return userService.lambdaQuery().eq(WmUser::getName, name).getEntity();
    }

    @PostMapping("/api/v1/wm_user/save")
    @Override
    public ResponseResult saveWmUser(@RequestBody WmUser wmUser) {
        userService.save(wmUser);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @PostMapping("/api/v1/wm_sensitive/check")
    @Override
    public ResponseResult checkSensitive(String content) {
        return wmSensitiveService.checkSensitive(content);
    }
}
