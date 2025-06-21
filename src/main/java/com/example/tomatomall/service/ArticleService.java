package com.example.tomatomall.service;

import com.example.tomatomall.vo.ArticleVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ArticleService {
    
    /**
     * 创建文章
     */
    ArticleVO createArticle(ArticleVO articleVO);

    
    /**
     * 分页获取所有文章
     */
    Page<ArticleVO> getAllArticles(Pageable pageable);


    /**
     * 增加文章阅读数
     */
    String incrementReadCount(String articleId);
    
    /**
     * 点赞文章
     */
    String likeArticle(String articleId);
    
    /**
     * 取消点赞文章
     */
    String unlikeArticle(String articleId);

    /**
     * 更新文章评论数
     */
    String updateCommentCount(String articleId, Integer commentCount);
}
