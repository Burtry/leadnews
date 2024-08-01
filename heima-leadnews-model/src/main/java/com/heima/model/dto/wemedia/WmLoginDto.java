package com.heima.model.dto.wemedia;


import lombok.Data;

@Data
public class WmLoginDto {

    /**
     * 用户名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
}
