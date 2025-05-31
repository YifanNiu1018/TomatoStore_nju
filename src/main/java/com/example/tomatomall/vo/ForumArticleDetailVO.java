package com.example.tomatomall.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ForumArticleDetailVO {

    private ForumArticleVO forumArticle;
    private Boolean haveLike = false;
}
