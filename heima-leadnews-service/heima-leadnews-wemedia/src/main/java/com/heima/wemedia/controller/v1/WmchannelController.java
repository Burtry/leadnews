package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.AdminChannelPageRequestDTO;
import com.heima.model.common.dtos.AdminLoginDTO;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channel")
public class WmchannelController {

    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult findAll(){
        return wmChannelService.findAll();
    }


    @PostMapping("/list")
    public ResponseResult getList(@RequestBody AdminChannelPageRequestDTO adminChannelPageRequestDTO) {
        return wmChannelService.getList(adminChannelPageRequestDTO);
    }

}