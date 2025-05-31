package com.example.tomatomall.po;

import com.example.tomatomall.vo.ForumArticleVO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class ForumAritcle {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "article_id")
    private String articleId;

    @Column(name = "board_id")
    private Integer boardId;

    @Column(name = "board_name")
    private String boardName;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "nick_name")
    private String nickName;

    @Column(name = "avatar")
    private String title;

    @Column(name = "title")
    private String cover;

    @Column(name = "content")
    private String content;

    @Column(name = "markdown_content")
    private String markdownContent;

    @Column(name = "editor_type")
    private Integer editorType;

    @Column(name = "summary")
    private String summary;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "post_time")
    private Date postTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    @Column(name = "read_count")
    private Integer readCount;

    @Column(name = "good_count")
    private Integer goodCount;

    @Column(name = "comment_count")
    private Integer commentCount;

    /**
     * 0未置顶  1:已置顶
     */
    @Column(name = "top_type")
    private Integer topType;

    public ForumArticleVO toVO() {
        ForumArticleVO forumArticleVO = new ForumArticleVO();
        forumArticleVO.setArticleId(this.articleId);
        forumArticleVO.setBoardId(this.boardId);
        forumArticleVO.setBoardName(this.boardName);
        forumArticleVO.setUserId(this.userId);
        forumArticleVO.setNickName(this.nickName);
        forumArticleVO.setTitle(this.title);
        forumArticleVO.setCover(this.cover);
        forumArticleVO.setContent(this.content);
        forumArticleVO.setSummary(this.summary);
        forumArticleVO.setPostTime(this.postTime);
        forumArticleVO.setReadCount(this.readCount);
        forumArticleVO.setGoodCount(this.goodCount);
        forumArticleVO.setCommentCount(this.commentCount);
        forumArticleVO.setTopType(this.topType);
        return forumArticleVO;
    }

}
