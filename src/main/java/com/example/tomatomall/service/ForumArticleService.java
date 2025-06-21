package com.example.tomatomall.service;

import com.example.tomatomall.vo.ForumArticleVO;

public interface ForumArticleService {
    ForumArticleVO loadArticle(Integer boardId, Integer orderType, Integer pageNum);

    ForumArticleVO getArticleDetail(Integer articleId);

    String doLike(Integer articleId);

    String postArticle(Integer boardId, String title, String content);

    ForumArticleVO articleDetail4Update(Integer articleId);

    String updateArticle(Integer articleId, Integer boardId, String title, String content);

    ForumArticleVO search(String keyword);
}
