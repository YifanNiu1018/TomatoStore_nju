package com.example.tomatomall.repository;

import com.example.tomatomall.po.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ArticleRepository extends JpaRepository<Article, String> {

    // 分页查询所有文章，按置顶和发布时间排序
    @Query("SELECT a FROM Article a ORDER BY a.postTime DESC")
    Page<Article> findAllOrderByTopTypeAndPostTime(Pageable pageable);
    
    // 根据板块ID分页查询，按置顶和发布时间排序
    @Query("SELECT a FROM Article a WHERE a.boardId = :boardId ORDER BY a.topType DESC, a.postTime DESC")
    Page<Article> findByBoardIdOrderByTopTypeAndPostTime(@Param("boardId") Integer boardId, Pageable pageable);
    
    // 增加阅读数
    @Modifying
    @Query("UPDATE Article a SET a.readCount = a.readCount + 1 WHERE a.articleId = :articleId")
    int incrementReadCount(@Param("articleId") String articleId);
    
    // 增加点赞数
    @Modifying
    @Query("UPDATE Article a SET a.goodCount = a.goodCount + 1 WHERE a.articleId = :articleId")
    int incrementGoodCount(@Param("articleId") String articleId);
    
    // 减少点赞数
    @Modifying
    @Query("UPDATE Article a SET a.goodCount = a.goodCount - 1 WHERE a.articleId = :articleId AND a.goodCount > 0")
    int decrementGoodCount(@Param("articleId") String articleId);
    
    // 更新评论数
    @Modifying
    @Query("UPDATE Article a SET a.commentCount = :commentCount WHERE a.articleId = :articleId")
    int updateCommentCount(@Param("articleId") String articleId, @Param("commentCount") Integer commentCount);

}
