package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.wemedia.NewsAuthPageReqDTO;
import com.heima.model.dto.wemedia.WmNewsDto;
import com.heima.model.dto.wemedia.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {


    @Autowired
    private WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto){
        return  wmNewsService.findAll(dto);
    }

    @PostMapping("/submit")
    public ResponseResult submitNews(@RequestBody WmNewsDto dto){
        return wmNewsService.suubmit(dto);
    }

    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto){
        return wmNewsService.downOrUp(dto);
    }

    @PostMapping("/list_vo")
    public ResponseResult getList(@RequestBody NewsAuthPageReqDTO newsAuthPageReqDTO) {
        return wmNewsService.getList(newsAuthPageReqDTO);
    }

    @GetMapping("/one_vo/{id}")
    public ResponseResult getOne(@PathVariable Integer id) {
        return wmNewsService.getNewsById(id);
    }

    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody NewsAuthPageReqDTO newsAuthPageReqDTO) {
        return wmNewsService.authFail(newsAuthPageReqDTO);
    }

    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody NewsAuthPageReqDTO newsAuthPageReqDTO) {
        return wmNewsService.authPass(newsAuthPageReqDTO);
    }


}