package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.exception.TomatoMailException;
import com.example.tomatomall.po.Article;
import com.example.tomatomall.repository.ArticleRepository;
import com.example.tomatomall.service.ArticleService;
import com.example.tomatomall.utils.SecurityUtil;
import com.example.tomatomall.vo.ArticleVO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final SecurityUtil securityUtil;

    public ArticleServiceImpl(ArticleRepository articleRepository, SecurityUtil securityUtil) {
        this.articleRepository = articleRepository;
        this.securityUtil = securityUtil;
    }

    @Override
    @Transactional
    public ArticleVO createArticle(ArticleVO articleVO) {
        String articleId = generateArticleId();
        articleVO.setArticleId(articleId);

        // 设置当前用户信息
        if (securityUtil.getCurrentUser() != null) {
            articleVO.setUserId(securityUtil.getCurrentUser().getId().toString());
            articleVO.setNickName(securityUtil.getCurrentUser().getName());
        }

        articleVO.setPostTime(LocalDateTime.now());

        if (articleVO.getReadCount() == null) {
            articleVO.setReadCount(0);
        }
        if (articleVO.getGoodCount() == null) {
            articleVO.setGoodCount(0);
        }
        if (articleVO.getCommentCount() == null) {
            articleVO.setCommentCount(0);
        }

        Article article = articleVO.toPO();
        article = articleRepository.save(article);

        return article.toVO();
    }



    @Override
    public Page<ArticleVO> getAllArticles(Pageable pageable) {
        Page<Article> articles = articleRepository.findAllOrderByTopTypeAndPostTime(pageable);
        return articles.map(Article::toVO);
    }



    @Override
    @Transactional
    public String incrementReadCount(String articleId) {
        int updated = articleRepository.incrementReadCount(articleId);
        if (updated > 0) {
            return "阅读数增加成功";
        } else {
            throw new RuntimeException("文章不存在");
        }
    }

    @Override
    @Transactional
    public String likeArticle(String articleId) {
        int updated = articleRepository.incrementGoodCount(articleId);
        if (updated > 0) {
            return "点赞成功";
        } else {
            throw new RuntimeException("文章不存在");
        }
    }

    @Override
    @Transactional
    public String unlikeArticle(String articleId) {
        int updated = articleRepository.decrementGoodCount(articleId);
        if (updated > 0) {
            return "取消点赞成功";
        } else {
            throw new RuntimeException("操作失败");
        }
    }


    @Override
    @Transactional
    public String updateCommentCount(String articleId, Integer commentCount) {
        int updated = articleRepository.updateCommentCount(articleId, commentCount);
        if (updated > 0) {
            return "评论数更新成功";
        } else {
            throw new RuntimeException("文章不存在");
        }
    }

    /**
     * 生成文章ID
     */
    private String generateArticleId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 15);
    }

}
