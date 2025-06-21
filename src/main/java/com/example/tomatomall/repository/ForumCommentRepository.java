package com.example.tomatomall.repository;

import com.example.tomatomall.po.ForumComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumCommentRepository extends JpaRepository<ForumComment, Integer> {
}
