package com.heima.service.impl;
 
import com.alibaba.fastjson.JSON;
import com.heima.apis.article.IArticleClient;
import com.heima.apis.user.IUserClient;
import com.heima.apis.wemedia.IWemediaClient;

import com.heima.common.constants.HotArticleConstants;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.comment.CommentDto;
import com.heima.model.dto.comment.CommentLikeDto;
import com.heima.model.dto.comment.CommentSaveDto;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.model.pojo.article.ApArticleConfig;
import com.heima.model.pojo.comment.ApComment;
import com.heima.model.pojo.comment.ApCommentLike;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.vo.comment.CommentVo;
import com.heima.service.CommentService;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
 
import java.util.*;
import java.util.stream.Collectors;
 
@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    @Autowired
    private IUserClient userClient;
    @Autowired
    private IArticleClient articleClient;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private IWemediaClient wemediaClient;
 
    /**
     * 保存评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveComment(CommentSaveDto dto) {
        // 1. 检查参数
        if(dto == null || StringUtils.isBlank(dto.getContent()) || dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
 
        // 2. 检查文章是否可以评论
        if(!checkParam(dto.getArticleId())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "该文章评论权限已关闭");
        }
 
        // 3. 约束评论内容长度
        if(dto.getContent().length() > 140) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "评论内容不能超过140字");
        }
 
        // 4. 判断是否登录(从ThreadLocal获得的用户信息只有userId)
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
 
        // 5. 安全检查
        ResponseResult response = wemediaClient.checkSensitive(dto.getContent());
        if(!response.getCode().equals(200)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前评论中包含敏感词");
        }
 
        // 6. 保存评论
        ApUser dbUser = userClient.findUserById(user.getId());
        if(dbUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前登录信息有误");
        }
        ApComment apComment = new ApComment();
        apComment.setAuthorId(user.getId());
        apComment.setContent(dto.getContent());
        apComment.setCreatedTime(new Date());
        apComment.setEntryId(dto.getArticleId());
        apComment.setImage(dbUser.getImage());
        apComment.setAuthorName(dbUser.getName());
        apComment.setLikes(0);
        apComment.setReply(0);
        apComment.setType((short) 0);
        apComment.setFlag((short) 0);
        mongoTemplate.save(apComment);
 
        // 7. 发送消息，进行聚合
        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getArticleId());
        mess.setAdd(1);
        mess.setType(UpdateArticleMess.UpdateArticleType.COMMENT);
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC, JSON.toJSONString(mess));
 
        // 8. 结果返回
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
 
    private boolean checkParam(Long articleId) {
        // 是否可以评论
        ResponseResult configResult = articleClient.findArticleConfigByArticleId(articleId);
        if(!configResult.getCode().equals(200) || configResult.getData() == null) {
            return false;
        }
 
        ApArticleConfig apArticleConfig = JSON.parseObject(JSON.toJSONString(configResult.getData()), ApArticleConfig.class);
        if(apArticleConfig == null || !apArticleConfig.getIsComment()) {
            return false;
        }
 
        return true;
    }
 
 
    /**
     * 点赞评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult like(CommentLikeDto dto) {
        // 1. 检查参数
        if(dto == null || dto.getCommentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
 
        // 2. 判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
 
        // 3. 根据id查找评论
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
 
        // 4. 点赞
        if(apComment != null && dto.getOperation() == 0) {
            // 点赞，更新评论点赞数量
            apComment.setLikes(apComment.getLikes() + 1);
            mongoTemplate.save(apComment);
 
            // 保存评论点赞数据
            ApCommentLike apCommentLike = new ApCommentLike();
            apCommentLike.setCommentId(apComment.getId());
            apCommentLike.setAuthorId(user.getId());
            mongoTemplate.save(apCommentLike);
        } else {
            // 取消点赞，更新评论点赞数量
            int tmp = apComment.getLikes() - 1;
            tmp = tmp < 1 ? 0 : tmp;
            apComment.setLikes(tmp);
            mongoTemplate.save(apComment);
 
            // 删除评论点赞
            Query query = Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(user.getId()));
            mongoTemplate.remove(query, ApComment.class);
        }
 
 
        // 5. 结果返回
        Map<String, Object> result = new HashMap<>();
        result.put("likes", apComment.getLikes());
        return ResponseResult.okResult(result);
    }
 
 
    /**
     * 加载评论列表
     * @param dto
     * @return
     */
    @Override
    public ResponseResult findByArticleId(CommentDto dto) {
        // 1. 检查参数
        if(dto == null || dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        int size = 10;
 
        // 2. 加载数据
        // 当前文章的评论数据
        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("createdTime").lt(dto.getMinDate()));
        query.with(Sort.by(Sort.Direction.DESC, "createdTime")).limit(size);
        List<ApComment> list = mongoTemplate.find(query, ApComment.class);
 
        // 3. 封装数据返回
        // 3.1 用户未登录
        ApUser user = AppThreadLocalUtil.getUser();
        if(user == null) {
            return ResponseResult.okResult(list);
        }
 
        // 3.2 用户已登录
        // 查询当前评论中哪些数据被点赞了
        List<String> idList = list.stream().map(x -> x.getId()).collect(Collectors.toList());
        Query query1 = Query.query(Criteria.where("commentId").in(idList).and("authorId").is(user.getId()));
        List<ApCommentLike> apCommentLikes = mongoTemplate.find(query1, ApCommentLike.class);
        if(apCommentLikes == null) {
            return ResponseResult.okResult(list);
        }
 
        List<CommentVo> resultList = new ArrayList<>();
        list.forEach(x -> {
            CommentVo vo = new CommentVo();
            BeanUtils.copyProperties(x, vo);
            for (ApCommentLike apCommentLike : apCommentLikes) {
                if(x.getId().equals(apCommentLike.getCommentId())) {
                    vo.setOperation((short) 0);
                    break;
                }
            }
            resultList.add(vo);
        });
        return ResponseResult.okResult(resultList);
    }
}