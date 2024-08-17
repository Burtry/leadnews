package com.heima.model.common.dtos;

import lombok.Data;

@Data
public class UserAuthPageRequestDTO extends PageRequestDto{

    private Integer id;

    private String msg;

    private Integer status;
}
