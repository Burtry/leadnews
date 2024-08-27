package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.article.CollectionBehaviorDTO;
import com.heima.model.pojo.article.APCollection;

public interface ApCollectionService extends IService<APCollection> {

    /**
     * 收藏或取消收藏文章
     * @param collectionBehaviorDTO
     * @return
     */
    ResponseResult collcetion(CollectionBehaviorDTO collectionBehaviorDTO);


}
