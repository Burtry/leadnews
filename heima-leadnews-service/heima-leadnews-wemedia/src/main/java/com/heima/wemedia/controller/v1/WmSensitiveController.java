package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.PageRequestAndNameDTO;
import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.pojo.wemedia.WmSensitive;
import com.heima.wemedia.service.WmSensitiveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensitive")
@Slf4j
public class WmSensitiveController {
    @Autowired
    private WmSensitiveService wmSensitiveService;


    @PostMapping("/list")
    public ResponseResult getList(@RequestBody PageRequestAndNameDTO pageRequestAndNameDTO) {
        return wmSensitiveService.getList(pageRequestAndNameDTO);
    }


    @PostMapping("/save")
    public ResponseResult save(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.saveSensitive(wmSensitive);
    }


    @DeleteMapping("/del/{id}")
    public ResponseResult delete(@PathVariable Integer id) {
        return wmSensitiveService.deleteById(id);
    }

    @PostMapping("/update")
    public ResponseResult update(@RequestBody WmSensitive wmSensitive) {
        return wmSensitiveService.updateSensiticve(wmSensitive);
    }
}
