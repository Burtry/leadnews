package com.heima.model.dto.wemedia;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class NewsAuthPageReqDTO extends PageRequestDto {

    private Integer id;

    private String msg;

    private Integer status;

    private String title;

}
