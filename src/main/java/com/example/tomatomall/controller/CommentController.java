package com.example.tomatomall.controller;

import com.example.tomatomall.service.CommentService;
import com.example.tomatomall.vo.CommentVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 添加评论
     */
    @PostMapping
    public Response<CommentVO> addComment(@RequestBody CommentVO commentVO) {
        return Response.buildSuccess(commentService.addComment(commentVO));
    }

    /**
     * 回复评论
     */
    @PostMapping("/reply")
    public Response<CommentVO> replyComment(@RequestBody CommentVO commentVO) {
        return Response.buildSuccess(commentService.replyComment(commentVO));
    }

    /**
     * 获取产品的所有一级评论
     */
    @GetMapping("/product/{productId}")
    public Response<List<CommentVO>> getProductComments(@PathVariable Integer productId) {
        return Response.buildSuccess(commentService.getProductComments(productId));
    }

    /**
     * 获取评论的所有回复
     */
    @GetMapping("/{commentId}/replies")
    public Response<List<CommentVO>> getCommentReplies(@PathVariable Integer commentId) {
        return Response.buildSuccess(commentService.getCommentReplies(commentId));
    }

    /**
     * 获取产品的所有评论（包括一级和二级）
     */
    @GetMapping("/product/{productId}/all")
    public Response<Map<CommentVO, List<CommentVO>>> getAllProductComments(@PathVariable Integer productId) {
        return Response.buildSuccess(commentService.getAllProductComments(productId));
    }

    /**
     * 给评论点赞
     */
    @PostMapping("/{commentId}/like")
    public Response<Boolean> likeComment(@PathVariable Integer commentId) {
        return Response.buildSuccess(commentService.likeComment(commentId));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public Response<Boolean> deleteComment(@PathVariable Integer commentId) {
        return Response.buildSuccess(commentService.deleteComment(commentId));
    }
}