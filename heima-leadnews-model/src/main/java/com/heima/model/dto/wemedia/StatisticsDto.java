package com.heima.model.dto.wemedia;
 
import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;
 
import java.util.Date;
 
@Data
public class StatisticsDto extends PageRequestDto {
 
    private String beginDate;
    private String endDate;
    private Integer wmUserId;
}