package com.example.tomatomall.controller;

import com.example.tomatomall.service.ForumArticleService;
import com.example.tomatomall.utils.SecurityUtil;
import com.example.tomatomall.vo.ForumArticleVO;
import com.example.tomatomall.vo.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/forum")
public class ForumArticleController {

    final
    ForumArticleService forumArticleService;

    final
    SecurityUtil securityUtil;

    public ForumArticleController(ForumArticleService forumArticleService, SecurityUtil securityUtil) {
        this.forumArticleService = forumArticleService;
        this.securityUtil = securityUtil;
    }

    @RequestMapping("/loadArticle")
    public Response<ForumArticleVO> loadArticle(Integer boardId, Integer orderType, Integer pageNum) {
        ForumArticleVO article = forumArticleService.loadArticle(boardId, orderType, pageNum);
        return Response.buildSuccess(article);
    }

    @RequestMapping("/getArticleDetail")
    public Response<ForumArticleVO> getArticleDetail(Integer articleId) {
        ForumArticleVO articleDetail = forumArticleService.getArticleDetail(articleId);
        return Response.buildSuccess(articleDetail);
    }

    @RequestMapping("/doLike")
    public Response<String> doLike(Integer articleId) {
        String result = forumArticleService.doLike(articleId);
        return Response.buildSuccess(result);
    }

//    @RequestMapping("/loadBoard4Post")
//    public Response<ForumArticleVO> loadBoard4Post(Integer boardId) {
//        ForumArticleVO boardDetails = forumArticleService.loadBoard4Post(boardId);
//        return Response.buildSuccess(boardDetails);
//    }

    @RequestMapping("/postArticle")
    public Response<String> postArticle(MultipartFile cover, Integer boardId, String title,
                                        String content, String markdownContent, Integer editorType, String summary) {
        String result = forumArticleService.postArticle(boardId, title, content);
        return Response.buildSuccess(result);
    }

    @RequestMapping("/articleDetail4Update")
    public Response<ForumArticleVO> articleDetail4Update(Integer articleId) {
        ForumArticleVO articleDetail = forumArticleService.articleDetail4Update(articleId);
        return Response.buildSuccess(articleDetail);
    }

    @RequestMapping("/updateArticle")
    public Response<String> updateArticle(Integer articleId, MultipartFile cover, Integer boardId, String title,
                                           String content, String markdownContent, Integer editorType, String summary) {
        String result = forumArticleService.updateArticle(articleId, boardId, title, content);
        return Response.buildSuccess(result);
    }

    @RequestMapping("/search")
    public Response<ForumArticleVO> search(String keyword) {
        ForumArticleVO searchResults = forumArticleService.search(keyword);
        return Response.buildSuccess(searchResults);
    }
}
