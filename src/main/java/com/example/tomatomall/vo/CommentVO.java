package com.example.tomatomall.vo;

import com.example.tomatomall.po.Comment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class CommentVO {

    private Integer commentId;

    private Integer PCommentId;

    private Integer productId;

    private String content;

    private String imgPath;

    private String userId;

    private String nickName;

    private Date postTime;

    private Integer goodCount;

    public Comment toPO() {
        Comment comment = new Comment();
        comment.setCommentId(this.commentId);
        comment.setPCommentId(this.PCommentId);
        comment.setProductId(this.productId);
        comment.setContent(this.content);
        comment.setImgPath(this.imgPath);
        comment.setUserId(this.userId);
        comment.setNickName(this.nickName);
        comment.setPostTime(this.postTime);
        comment.setGoodCount(this.goodCount);
        return comment;
    }
}