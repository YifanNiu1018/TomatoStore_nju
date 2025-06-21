package com.example.tomatomall.repository;

import com.example.tomatomall.po.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    // 查找产品的一级评论（pCommentId为null）
    List<Comment> findByProductIdAndPCommentIdIsNull(Integer productId);

    List<Comment> findByPCommentId(Integer pCommentId);

    // 增加评论点赞数
    @Modifying
    @Query("UPDATE Comment c SET c.goodCount = c.goodCount + 1 WHERE c.commentId = ?1")
    int incrementGoodCount(Integer commentId);
}