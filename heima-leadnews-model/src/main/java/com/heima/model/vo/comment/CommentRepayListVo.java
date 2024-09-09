package com.heima.model.vo.comment;
 
import com.heima.model.pojo.comment.ApComment;
import com.heima.model.pojo.comment.ApCommentRepay;
import lombok.Data;
 
import java.util.List;
 
@Data
public class CommentRepayListVo  {
 
    private ApComment apComments;
    private List<ApCommentRepay> apCommentRepays;
}