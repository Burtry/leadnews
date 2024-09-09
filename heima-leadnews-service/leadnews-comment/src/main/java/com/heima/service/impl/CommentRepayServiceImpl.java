package com.heima.service.impl;

import com.heima.apis.user.IUserClient;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.dto.comment.CommentRepayDto;
import com.heima.model.dto.comment.CommentRepayLikeDto;
import com.heima.model.dto.comment.CommentRepaySaveDto;
import com.heima.model.pojo.comment.ApComment;
import com.heima.model.pojo.comment.ApCommentRepay;
import com.heima.model.pojo.comment.ApCommentRepayLike;
import com.heima.model.pojo.user.ApUser;
import com.heima.model.vo.comment.CommentRepayVo;
import com.heima.service.CommentRepayService;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CommentRepayServiceImpl implements CommentRepayService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IUserClient userClient;
    @Autowired
    private IWemediaClient wemediaClient;

    @Override
    public ResponseResult load(CommentRepayDto commentRepayDto) {
        //参数检查
        if (commentRepayDto == null || commentRepayDto.getCommentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //加载数据
        int size = 20;
        Query query = Query.query(Criteria.where("commentId").is(commentRepayDto.getCommentId()).and("createdTime").lt(commentRepayDto.getMinDate()));
        query.with(Sort.by(Sort.Direction.DESC,"createdTime")).limit(size);

        List<ApCommentRepay> list = mongoTemplate.find(query, ApCommentRepay.class);

        //用户为登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            ResponseResult.okResult(list);
        }

        //用户登录
        //查询当前评论哪些被点赞了
        List<String> idList = list.stream().map(ApCommentRepay::getId).collect(Collectors.toList());
        Query query1 = Query.query(Criteria.where("commentId").in(idList).and("authorId").is(user.getId()));
        List<ApCommentRepayLike> appCommentRepayLikes = mongoTemplate.find(query1, ApCommentRepayLike.class);
        if (appCommentRepayLikes.isEmpty()) {
            return ResponseResult.okResult(list);
        }

        ArrayList<CommentRepayVo> resultList = new ArrayList<>();
        list.forEach(x -> {
            CommentRepayVo commentRepayVo = new CommentRepayVo();
            BeanUtils.copyProperties(x,commentRepayVo);

            for (ApCommentRepayLike appCommentRepayLike : appCommentRepayLikes) {
                if (x.getId().equals(appCommentRepayLike.getCommentRepayId())) {
                    commentRepayVo.setOperation((short) 0 );
                }
                break;
            }
            resultList.add(commentRepayVo);
        });

        return ResponseResult.okResult(resultList);
    }

    @Override
    public ResponseResult save(CommentRepaySaveDto dto) {
        if (dto == null || dto.getCommentId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (dto.getContent().length() > 140) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"评论不能超过140字");
        }

        //判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //安全检查
        ResponseResult responseResult = wemediaClient.checkSensitive(dto.getContent());

        if (!responseResult.getCode().equals(200)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID,"参数中包含敏感词");
        }

        //保存评论
        ApCommentRepay apCommentRepay = new ApCommentRepay();
        apCommentRepay.setAuthorId(user.getId());
        apCommentRepay.setContent(dto.getContent());
        apCommentRepay.setAuthorName(user.getName());
        apCommentRepay.setCreatedTime(new Date());
        apCommentRepay.setCommentId(dto.getCommentId());
        apCommentRepay.setUpdatedTime(new Date());
        apCommentRepay.setLikes(0);

        mongoTemplate.save(apCommentRepay);


        //更新回复数量

        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        apComment.setReply(apComment.getReply() + 1);
        mongoTemplate.save(apComment);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult like(CommentRepayLikeDto commentRepayLikeDto) {

        if (commentRepayLikeDto == null || commentRepayLikeDto.getCommentRepayId() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        ApCommentRepay apCommentRepay = mongoTemplate.findById(commentRepayLikeDto.getCommentRepayId(), ApCommentRepay.class);

        //点赞

        if (apCommentRepay != null && commentRepayLikeDto.getOperation() == 0) {
            ///更新评论点赞数量
            apCommentRepay.setLikes(apCommentRepay.getLikes() + 1);
            mongoTemplate.save(apCommentRepay);

            //保存评论点赞数据
            ApCommentRepayLike apCommentRepayLike = new ApCommentRepayLike();
            apCommentRepayLike.setCommentRepayId(apCommentRepay.getId());
            apCommentRepayLike.setAuthorId(user.getId());
            mongoTemplate.save(apCommentRepayLike);
        } else {
            int tmp = apCommentRepay.getLikes() - 1;
            tmp = tmp < 1 ? 0 : tmp;

            apCommentRepay.setLikes(tmp);

            mongoTemplate.save(apCommentRepay);

            //删除评论点赞
            Query query = Query.query(Criteria.where("commentRepayId").is(apCommentRepay.getId()).and("authorId").is(user.getId()));

            mongoTemplate.remove(query,ApCommentRepayLike.class);
        }

        HashMap<String, Object> result = new HashMap<>();
        result.put("likes",apCommentRepay.getLikes());
        return ResponseResult.okResult(result);
    }
}
