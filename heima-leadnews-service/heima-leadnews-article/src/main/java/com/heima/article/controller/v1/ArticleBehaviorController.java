package com.heima.article.controller.v1;

import com.heima.article.service.ApCollectionService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.dto.article.CollectionBehaviorDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ArticleBehaviorController {

    @Autowired
    private ApCollectionService apCollectionService;

    @PostMapping("/collection_behavior")
    public ResponseResult collectionBehavior(@RequestBody CollectionBehaviorDTO collectionBehaviorDTO) {
        return apCollectionService.collcetion(collectionBehaviorDTO);
    }


}
