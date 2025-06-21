package com.example.tomatomall.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "forum_comments")
public class ForumComment {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "comment_id")
    private Integer commentId;

    @Column(name = "p_comment_id")
    private Integer pCommentId;

    @Column(name = "article_id")
    private String articleId;

    @Column(name = "content")
    private String content;

    @Column(name = "img_path")
    private String imgPath;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "reply_user_id")
    private String replyUserId;

    @Column(name = "reply_nick_name")
    private String replyNickName;

    @Column(name = "top_type")
    private Integer topType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "post_time")
    private Date postTime;

    @Column(name = "good_count")
    private Integer goodCount;

    @Column(name = "like_type")
    private Integer likeType;


    private List<ForumComment> children;
}
