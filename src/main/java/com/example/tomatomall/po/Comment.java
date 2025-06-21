package com.example.tomatomall.po;

import com.example.tomatomall.vo.CommentVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "comments")
public class Comment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "comment_id")
    private Integer commentId;

    //父级评论ID，如果是一级评论则为0
    @Column(name = "p_comment_id")
    private Integer PCommentId;

    @Column(name = "product_id", nullable = false)
    private Integer productId;

    @Column(name = "content")
    private String content;

    @Column(name = "img_path")
    private String imgPath;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "post_time")
    private Date postTime;

    @Column(name = "good_count")
    private Integer goodCount = 0;

    public CommentVO toVO() {
        CommentVO commentVO = new CommentVO();
        commentVO.setCommentId(this.commentId);
        commentVO.setPCommentId(this.PCommentId);
        commentVO.setProductId(this.productId);
        commentVO.setContent(this.content);
        commentVO.setImgPath(this.imgPath);
        commentVO.setUserId(this.userId);
        commentVO.setNickName(this.nickName);
        commentVO.setPostTime(this.postTime);
        commentVO.setGoodCount(this.goodCount);
        return commentVO;
    }
}