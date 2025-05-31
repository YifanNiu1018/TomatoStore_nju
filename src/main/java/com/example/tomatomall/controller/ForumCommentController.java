package com.example.tomatomall.controller;

import com.example.tomatomall.service.ForumCommentService;
import com.example.tomatomall.service.LikeRecordService;
import com.example.tomatomall.vo.ForumCommentVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/comment")
public class ForumCommentController {

    final
    ForumCommentService forumCommentService;

    final
    LikeRecordService likeRecordService;

    public ForumCommentController(ForumCommentService forumCommentService, LikeRecordService likeRecordService) {
        this.forumCommentService = forumCommentService;
        this.likeRecordService = likeRecordService;
    }

    @RequestMapping("/loadComment")
    public Response loadComment(String articleId, Integer pageNo, Integer orderType) {
        return Response.buildSuccess(forumCommentService.loadComment(articleId, pageNo, orderType));
    }

    @RequestMapping("/postComment")
    public Response<ForumCommentVO> postComment(String articleId, Integer pCommentId,
                                                String content, String replyUserId, MultipartFile image) {
        ForumCommentVO comment = forumCommentService.postComment(articleId, pCommentId, content, replyUserId, image);
        return Response.buildSuccess(comment);
    }

    @RequestMapping("/doLike")
    public Response<String> doLike(Integer commentId) {
        String result = likeRecordService.doLike(commentId);
        return Response.buildSuccess(result);
    }

    @RequestMapping("/changeTopType")
    public Response<String> changeTopType(Integer commentId, Integer topType) {
        String result = forumCommentService.changeTopType(commentId, topType);
        return Response.buildSuccess(result);
    }

}
