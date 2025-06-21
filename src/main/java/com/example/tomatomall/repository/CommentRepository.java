package com.example.tomatomall.repository;

import com.example.tomatomall.po.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 查找产品的一级评论（pCommentId为null）
    List<Comment> findByProductIdAndPCommentIdIsNull(Integer productId);

    // 查找特定产品下特定父评论的所有回复
    List<Comment> findByProductIdAndPCommentId(Integer productId, Integer pCommentId);

    // 查找指定父评论下的所有回复
    List<Comment> findByPCommentId(Integer pCommentId);

    // 增加评论点赞数
    @Modifying
    @Query("UPDATE Comment c SET c.goodCount = c.goodCount + 1 WHERE c.commentId = ?1")
    int incrementGoodCount(Integer commentId);
}