package com.heima.model.pojo.user;

import lombok.Data;

import java.util.Date;

@Data
public class ApUserFollow {

    private Long id;

    private Integer userId;

    private Integer followId;

    private String followName;

    private Short level;

    private Short isNotice;

    private Date createdTime;

}
