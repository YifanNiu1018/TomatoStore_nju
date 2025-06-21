package com.example.tomatomall.vo;

import com.example.tomatomall.po.Article;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ArticleVO {
    private String articleId;
    private String userId;
    private String nickName;
    private String title;
    private String cover;
    private String content;
    private String markdownContent;
    private Integer editorType; // 0:富文本编辑器 1:markdown编辑器
    private String summary;
    private LocalDateTime postTime;
    private Integer readCount;
    private Integer goodCount;
    private Integer commentCount;

    public Article toPO() {
        Article article = new Article();
        article.setArticleId(this.articleId);
        article.setUserId(this.userId);
        article.setNickName(this.nickName);
        article.setTitle(this.title);
        article.setCover(this.cover);
        article.setContent(this.content);
        article.setMarkdownContent(this.markdownContent);
        article.setEditorType(this.editorType);
        article.setSummary(this.summary);
        article.setPostTime(this.postTime);
        article.setReadCount(this.readCount);
        article.setGoodCount(this.goodCount);
        article.setCommentCount(this.commentCount);
        return article;
    }
}
