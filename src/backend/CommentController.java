package com.example.tomatomall.controller;

import com.example.tomatomall.service.CommentService;
import com.example.tomatomall.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    /**
     * 添加评论
     */
    @PostMapping
    public ResponseEntity<CommentVO> addComment(@RequestBody CommentVO commentVO) {
        return ResponseEntity.ok(commentService.addComment(commentVO));
    }

    /**
     * 回复评论
     */
    @PostMapping("/reply")
    public ResponseEntity<CommentVO> replyComment(@RequestBody CommentVO commentVO) {
        return ResponseEntity.ok(commentService.replyComment(commentVO));
    }

    /**
     * 获取产品的所有一级评论
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommentVO>> getProductComments(@PathVariable Integer productId) {
        return ResponseEntity.ok(commentService.getProductComments(productId));
    }

    /**
     * 获取评论的所有回复
     */
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<List<CommentVO>> getCommentReplies(@PathVariable Integer commentId) {
        return ResponseEntity.ok(commentService.getCommentReplies(commentId));
    }

    /**
     * 获取产品的所有评论（包括一级和二级）
     */
    @GetMapping("/product/{productId}/all")
    public ResponseEntity<Map<CommentVO, List<CommentVO>>> getAllProductComments(@PathVariable Integer productId) {
        return ResponseEntity.ok(commentService.getAllProductComments(productId));
    }

    /**
     * 给评论点赞
     */
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Boolean> likeComment(@PathVariable Integer commentId) {
        return ResponseEntity.ok(commentService.likeComment(commentId));
    }

    /**
     * 删除评论
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Boolean> deleteComment(@PathVariable Integer commentId) {
        return ResponseEntity.ok(commentService.deleteComment(commentId));
    }
}