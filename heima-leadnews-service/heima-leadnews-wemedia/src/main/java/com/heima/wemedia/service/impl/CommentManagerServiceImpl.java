package com.heima.wemedia.service.impl;
 
import com.heima.apis.article.IArticleClient;
import com.heima.apis.user.IUserClient;
import com.heima.apis.wemedia.IWemediaClient;

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;

import com.heima.model.dto.comment.*;
import com.heima.model.pojo.comment.ApComment;
import com.heima.model.pojo.comment.ApCommentLike;
import com.heima.model.pojo.comment.ApCommentRepay;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.pojo.wemedia.WmUser;
import com.heima.model.vo.comment.CommentRepayListVo;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.CommentManagerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
 
import java.util.*;
 
@Service
@Slf4j
public class CommentManagerServiceImpl implements CommentManagerService {
 
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IArticleClient articleClient;
    @Autowired
    private IUserClient userClient;
    @Autowired
    private WmUserMapper wmUserMapper;
    @Autowired
    private IWemediaClient wemediaClient;
 
    /**
     * 查询评论回复列表
     * @param dto
     * @return
     */
    @Override
    public ResponseResult list(CommentManageDto dto) {
        // 1. 检查参数
        if(dto.getArticleId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
 
        // 2. 检查分页
        dto.checkParam();
 
        List<CommentRepayListVo> commentRepayListVoList = new ArrayList<>();
 
        // 3. 根据文章id查询评论
        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()));
        Pageable pageable = PageRequest.of(dto.getPage(), dto.getSize());
        query.with(pageable);
        query.with(Sort.by(Sort.Direction.DESC, "createdTime"));
 
        List<ApComment> list = mongoTemplate.find(query, ApComment.class);
 
        // 4. 查询每条评论的回复评论
        for (ApComment apComment : list) {
            CommentRepayListVo vo = new CommentRepayListVo();
            vo.setApComments(apComment);
            Query query1 = Query.query(Criteria.where("commentId").is(apComment.getId()));
            query1.with(Sort.by(Sort.Direction.DESC, "createdTime"));
 
            List<ApCommentRepay> apCommentRepays = mongoTemplate.find(query1, ApCommentRepay.class);
            vo.setApCommentRepays(apCommentRepays);
            commentRepayListVoList.add(vo);
        }
 
        // 5. 结果返回
        return ResponseResult.okResult(commentRepayListVoList);
    }
 
    /**
     * 删除评论
     * @param commentId
     * @return
     */
    @Override
    public ResponseResult delComment(String commentId) {
        // 1. 检查参数
        if(StringUtils.isBlank(commentId)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "评论id不能为空");
        }
 
        // 2. 删除评论
        mongoTemplate.remove(Query.query(Criteria.where("id").is(commentId)), ApComment.class);
 
        // 3. 删除该评论的所有回复内容
        mongoTemplate.remove(Query.query(Criteria.where("commentId").is(commentId)), ApCommentRepay.class);
 
        // 4. 结果返回
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
 
    /**
     * 删除评论回复
     * @param commentRepayId
     * @return
     */
    @Override
    public ResponseResult delCommentRepay(String commentRepayId) {
        // 1. 检查参数
        if(StringUtils.isBlank(commentRepayId)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "评论回复id不能为空");
        }
 
        // 2. 删除评论回复
        mongoTemplate.remove(Query.query(Criteria.where("id").is(commentRepayId)), ApCommentRepay.class);
 
        // 3. 结果返回
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
 
    /**
     * 分页查询文章评论列表
     * @param dto
     * @return
     */
    @Override
    public PageResponseResult findNewsComments(ArticleCommentDto dto) {
        // 1. 获取登录用户
        WmUser user = WmThreadLocalUtil.getUser();
        dto.setWmUserId(user.getId());
 
        // 2. 远程调用
        return articleClient.findNewsComments(dto);
    }
 
    /**
     * 修改文章评论配置 打开或关闭评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult updateCommentStatus(CommentConfigDto dto) {
        // 1. 获取当前登录用户
        WmUser wmUser = WmThreadLocalUtil.getUser();
 
        // 2. app端用户id
        WmUser dbUser = wmUserMapper.selectById(wmUser.getId());
        Integer apUserId = dbUser.getApUserId();
 
        // 3. 清空该文章的所有评论
        // 个人认为不需要清空关闭评论之前的评论数据
        /*List<ApComment> apCommentList = mongoTemplate.find(Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("authorId").is(apUserId)), ApComment.class);
        for (ApComment apComment : apCommentList) {
            List<ApCommentRepay> commentRepayList = mongoTemplate.find(Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(apUserId)), ApCommentRepay.class);
            List<String> commentRepayIdList = commentRepayList.stream().map(ApCommentRepay::getId).distinct().collect(Collectors.toList());
            //删除所有的评论回复点赞数据
            mongoTemplate.remove(Query.query(Criteria.where("commentRepayId").in(commentRepayIdList).and("authorId").is(apUserId)), ApCommentRepayLike.class);
            //删除该评论的所有的回复内容
            mongoTemplate.remove(Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("authorId").is(apUserId)), ApCommentRepay.class);
            //删除评论的点赞
            mongoTemplate.remove(Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(apUserId)), ApCommentLike.class);
        }
        // 4. 删除评论
        mongoTemplate.remove(Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("authorId").is(apUserId)),ApComment.class);*/
 
        // 5. 修改app文章的config配置
        return articleClient.updateCommentStatus(dto);
    }
 
    /**
     * 回复评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveCommentRepay(CommentRepaySaveDto dto) {
        // 1. 检查参数
        if(dto == null || StringUtils.isBlank(dto.getContent()) || dto.getCommentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if(dto.getContent().length() > 140) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "评论内容不能超过140字");
        }
 
        // 2. 安全检查
        ResponseResult response = wemediaClient.checkSensitive(dto.getContent());
        if(!response.getCode().equals(200)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前评论包含违规内容");
        }
 
        // 3. 获取自媒体人信息
        WmUser wmUser = WmThreadLocalUtil.getUser();
        WmUser dbUser = wmUserMapper.selectById(wmUser.getId());
        if(dbUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
 
        // 4. 获取app端用户信息
        ApUser apUser = userClient.findUserById(dbUser.getApUserId());
 
        // 5. 保存评论
        ApCommentRepay apCommentRepay = new ApCommentRepay();
        apCommentRepay.setAuthorId(apUser.getId());
        apCommentRepay.setAuthorName(apUser.getName());
        apCommentRepay.setContent(dto.getContent());
        apCommentRepay.setCreatedTime(new Date());
        apCommentRepay.setCommentId(dto.getCommentId());
        apCommentRepay.setUpdatedTime(new Date());
        apCommentRepay.setLikes(0);
        mongoTemplate.save(apCommentRepay);
 
        // 6. 更新回复数量
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        apComment.setReply(apComment.getReply() + 1);
        mongoTemplate.save(apComment);
 
        // 7. 结果返回
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
 
    /**
     * 点赞评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult like(CommentLikeDto dto) {
        // 1. 检查参数
        if (dto == null || dto.getCommentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
 
        // 2. 查找评论
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
 
        // 3. 获取当前登录用户信息
        WmUser wmUser = WmThreadLocalUtil.getUser();
        WmUser dbUser = wmUserMapper.selectById(wmUser.getId());
        if(dbUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
 
        // 4. 获取app端用户信息
        ApUser apUser = userClient.findUserById(dbUser.getApUserId());
 
        // 5. 点赞
        if(apComment != null && dto.getOperation() == 0) {
            // 更新评论点赞数量
            apComment.setLikes(apComment.getLikes() + 1);
            mongoTemplate.save(apComment);
 
            // 保存评论点赞数据
            ApCommentLike apCommentLike = new ApCommentLike();
            apCommentLike.setCommentId(apComment.getId());
            apCommentLike.setAuthorId(apUser.getId());
            mongoTemplate.save(apCommentLike);
        } else {
            //更新评论点赞数量
            int tmp = apComment.getLikes() - 1;
            tmp = tmp < 1 ? 0 : tmp;
            apComment.setLikes(tmp);
            mongoTemplate.save(apComment);
 
            //删除评论点赞
            Query query = Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(apUser.getId()));
            mongoTemplate.remove(query, ApCommentLike.class);
        }
 
        //4.取消点赞
        Map<String, Object> result = new HashMap<>();
        result.put("likes", apComment.getLikes());
        return ResponseResult.okResult(result);
    }
}