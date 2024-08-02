package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.wemedia.WmMaterialDto;
import com.heima.model.pojo.wemedia.WmMaterial;

import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {

        //校验参数
        if (multipartFile == null || multipartFile.getSize() == 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //上传图片到Minio
        String fileName = UUID.randomUUID().toString().replaceAll("-", "");
        String originalFilename = multipartFile.getOriginalFilename();
            //获取原始文件的后缀名
        int postfix = originalFilename.lastIndexOf(".");

        String fileId = null;
        try {
            fileId = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("上传图片至MinIO,fileId:{}",fileId);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("WmMaterialServiceImpl" + "上传文件失败");
        }

        //保存数据到数据库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setType((short)0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        return ResponseResult.okResult(wmMaterial);

    }

    /**
     * 素材列表查询
     * @param wmMaterialDto
     * @return
     */
    @Override
    public ResponseResult getList(WmMaterialDto wmMaterialDto) {

        //参数检查
        wmMaterialDto.checkParam();
        //分页查询
        IPage page = new Page(wmMaterialDto.getPage(), wmMaterialDto.getSize());

        LambdaQueryWrapper<WmMaterial> wmMaterialLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //是否收藏
        if (wmMaterialDto.getIsCollection() != null && wmMaterialDto.getIsCollection() == 1) {
            wmMaterialLambdaQueryWrapper.eq(WmMaterial::getIsCollection,wmMaterialDto.getIsCollection());
        }

        //按照用户查询
        wmMaterialLambdaQueryWrapper.eq(WmMaterial::getUserId,WmThreadLocalUtil.getUser().getId());

        //按照时间倒序
        wmMaterialLambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);
        page = page(page,wmMaterialLambdaQueryWrapper);

        //结果返回
        ResponseResult responseResult = new PageResponseResult(wmMaterialDto.getPage(), wmMaterialDto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }
}
