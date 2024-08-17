package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.PageRequestAndNameDTO;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.wemedia.AdChannelDTO;
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
    public ResponseResult getList(@RequestBody PageRequestAndNameDTO pageRequestAndNameDTO) {
        return wmChannelService.getList(pageRequestAndNameDTO);
    }


    @PostMapping("/save")
    public ResponseResult save(@RequestBody AdChannelDTO adChannelDTO) {
        return wmChannelService.saveChannel(adChannelDTO);
    }


    @GetMapping("/del/{id}")
    public ResponseResult delete(@PathVariable Integer id) {
        return wmChannelService.delete(id);
    }


    @PostMapping("/update")
    public ResponseResult update(@RequestBody AdChannelDTO adChannelDTO) {

        return wmChannelService.updateChannel(adChannelDTO);
    }

}