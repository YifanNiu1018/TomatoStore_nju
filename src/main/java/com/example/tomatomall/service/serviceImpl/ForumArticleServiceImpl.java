package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.ForumArticleService;
import com.example.tomatomall.vo.ForumArticleVO;
import org.springframework.stereotype.Service;

@Service
public class ForumArticleServiceImpl implements ForumArticleService {

    @Override
    public ForumArticleVO loadArticle(Integer boardId, Integer orderType, Integer pageNum) {
        return null;
    }

    @Override
    public ForumArticleVO getArticleDetail(Integer articleId) {
        return null;
    }

    @Override
    public String doLike(Integer articleId) {
        return "";
    }

    @Override
    public String postArticle(Integer boardId, String title, String content) {
        return "";
    }

    @Override
    public ForumArticleVO articleDetail4Update(Integer articleId) {
        return null;
    }

    @Override
    public String updateArticle(Integer articleId, Integer boardId, String title, String content) {
        return "";
    }

    @Override
    public ForumArticleVO search(String keyword) {
        return null;
    }

}
