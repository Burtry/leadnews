package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.wemedia.WmMaterialDto;
import com.heima.model.pojo.wemedia.WmMaterial;
import org.springframework.web.multipart.MultipartFile;

public interface WmMaterialService extends IService<WmMaterial> {
    ResponseResult uploadPicture(MultipartFile multipartFile);

    ResponseResult getList(WmMaterialDto wmMaterialDto);
}
