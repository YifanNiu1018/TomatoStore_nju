package com.example.tomatomall.service;

import com.example.tomatomall.vo.ForumBoardVO;

import java.util.List;

public interface ForumBoardService {
    List<ForumBoardVO> loadBoard();

    String createBoard(ForumBoardVO forumBoardVO);

    String joinBoard(Integer boardId);

    Integer getJoinedCount(Integer boardId);

    String getUserStatus(Integer boardId);

    String getUserStatus1(Integer boardId);

    String getUserStatus2(Integer boardId);
}
