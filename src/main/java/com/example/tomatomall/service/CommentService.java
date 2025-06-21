package com.example.tomatomall.service;

import com.example.tomatomall.vo.CommentVO;

import java.util.List;
import java.util.Map;

public interface CommentService {

    /**
     * 添加新评论
     */
    CommentVO addComment(CommentVO commentVO);

    /**
     * 回复评论（添加二级评论）
     */
    CommentVO replyComment(CommentVO commentVO);

    /**
     * 获取产品的所有一级评论
     */
    List<CommentVO> getProductComments(Integer productId);

    /**
     * 获取评论的所有回复
     */
    List<CommentVO> getCommentReplies(Integer commentId);

    /**
     * 获取产品的所有评论（包括一级评论和对应的回复）
     */
    Map<CommentVO, List<CommentVO>> getAllProductComments(Integer productId);

    /**
     * 对评论点赞
     */
    boolean likeComment(Integer commentId);

    /**
     * 删除评论
     */
    boolean deleteComment(Integer commentId);
}