package com.example.tomatomall.vo;

import com.example.tomatomall.po.ForumAritcle;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ForumArticleVO {

    private String articleId;
    private Integer boardId;
    private String boardName;
    private String userId;
    private String nickName;
    private String title;
    private String cover;
    private String content;
    private String summary;
    private Date postTime;
    private Integer readCount;
    private Integer goodCount;
    private Integer commentCount;
    private Integer topType;

    public ForumAritcle toPO() {
        ForumAritcle forumAritcle = new ForumAritcle();
        forumAritcle.setArticleId(this.articleId);
        forumAritcle.setBoardId(this.boardId);
        forumAritcle.setUserId(this.userId);
        forumAritcle.setTitle(this.title);
        forumAritcle.setCover(this.cover);
        forumAritcle.setContent(this.content);
        forumAritcle.setSummary(this.summary);
        forumAritcle.setPostTime(this.postTime);
        forumAritcle.setReadCount(this.readCount);
        forumAritcle.setGoodCount(this.goodCount);
        forumAritcle.setCommentCount(this.commentCount);
        forumAritcle.setTopType(this.topType);
        return forumAritcle;
    }
}
