package com.heima.model.dto.wemedia;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

@Data
public class WmMaterialDto extends PageRequestDto {

    /**
     * 0未收藏
     * 1已收藏
     */
    private Short isCollection;
}
