package com.example.tomatomall.po;

import com.example.tomatomall.vo.ArticleVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "articles")
public class Article {

    @Id
    @Column(name = "article_id")
    private String articleId;

    @NotBlank
    @Column(name = "user_id")
    private String userId;

    @NotBlank
    @Column(name = "nick_name")
    private String nickName;

    @NotBlank
    @Column(name = "title")
    private String title;

    @Column(name = "cover")
    private String cover;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "markdown_content", columnDefinition = "TEXT")
    private String markdownContent;

    @NotNull
    @Column(name = "editor_type")
    private Integer editorType; // 0:富文本编辑器 1:markdown编辑器

    @Column(name = "summary")
    private String summary;

    @NotNull
    @Column(name = "post_time")
    private LocalDateTime postTime;


    @Column(name = "read_count")
    private Integer readCount = 0;

    @Column(name = "good_count")
    private Integer goodCount = 0;

    @Column(name = "comment_count")
    private Integer commentCount = 0;


    public ArticleVO toVO() {
        ArticleVO articleVO = new ArticleVO();
        articleVO.setArticleId(this.articleId);
        articleVO.setUserId(this.userId);
        articleVO.setNickName(this.nickName);
        articleVO.setTitle(this.title);
        articleVO.setCover(this.cover);
        articleVO.setContent(this.content);
        articleVO.setMarkdownContent(this.markdownContent);
        articleVO.setEditorType(this.editorType);
        articleVO.setSummary(this.summary);
        articleVO.setPostTime(this.postTime);
        articleVO.setReadCount(this.readCount);
        articleVO.setGoodCount(this.goodCount);
        articleVO.setCommentCount(this.commentCount);
        return articleVO;
    }
}
