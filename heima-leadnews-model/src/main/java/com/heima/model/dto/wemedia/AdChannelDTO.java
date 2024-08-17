package com.heima.model.dto.wemedia;

import lombok.Data;
import org.springframework.context.annotation.EnableMBeanExport;

@Data
public class AdChannelDTO {
    private Integer id;

    private String name;

    private String description;

    private boolean isDefault;

    private Integer ord;

    private boolean status;

    private String createTime;
}
