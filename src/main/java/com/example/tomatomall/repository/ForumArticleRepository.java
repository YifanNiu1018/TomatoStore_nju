package com.example.tomatomall.repository;

import com.example.tomatomall.po.ForumAritcle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ForumArticleRepository extends JpaRepository<ForumAritcle, Integer> {

    ForumAritcle findByTitle(String title);
}
