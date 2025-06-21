package com.example.tomatomall.controller;

import com.example.tomatomall.service.ArticleService;
import com.example.tomatomall.vo.ArticleVO;
import com.example.tomatomall.vo.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 创建文章
     */
    @PostMapping
    public Response<ArticleVO> createArticle(@RequestBody ArticleVO articleVO) {
        try {
            ArticleVO createdArticle = articleService.createArticle(articleVO);
            return Response.buildSuccess(createdArticle);
        } catch (Exception e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }

    /**
     * 分页获取所有文章
     */
    @GetMapping
    public Response<Page<ArticleVO>> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "postTime") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<ArticleVO> articles = articleService.getAllArticles(pageable);
            return Response.buildSuccess(articles);
        } catch (Exception e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }


    /**
     * 增加文章阅读数
     */
    @PostMapping("/{articleId}/read")
    public Response<String> incrementReadCount(@PathVariable String articleId) {
        try {
            String result = articleService.incrementReadCount(articleId);
            return Response.buildSuccess(result);
        } catch (Exception e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }

    /**
     * 点赞文章
     */
    @PostMapping("/{articleId}/like")
    public Response<String> likeArticle(@PathVariable String articleId) {
        try {
            String result = articleService.likeArticle(articleId);
            return Response.buildSuccess(result);
        } catch (Exception e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }

    /**
     * 取消点赞文章
     */
    @DeleteMapping("/{articleId}/like")
    public Response<String> unlikeArticle(@PathVariable String articleId) {
        try {
            String result = articleService.unlikeArticle(articleId);
            return Response.buildSuccess(result);
        } catch (Exception e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }

    /**
     * 更新文章评论数
     */
    @PatchMapping("/{articleId}/comment-count")
    public Response<String> updateCommentCount(
            @PathVariable String articleId,
            @RequestParam Integer commentCount) {
        try {
            String result = articleService.updateCommentCount(articleId, commentCount);
            return Response.buildSuccess(result);
        } catch (Exception e) {
            return Response.buildFailure(e.getMessage(), "400");
        }
    }
}
