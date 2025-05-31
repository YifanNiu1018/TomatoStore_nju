package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.ForumBoardService;
import com.example.tomatomall.vo.ForumBoardVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ForumBoardServiceIpmpl implements ForumBoardService {
    @Override
    public List<ForumBoardVO> loadBoard() {
        return Collections.emptyList();
    }

    @Override
    public String createBoard(ForumBoardVO forumBoardVO) {
        return "";
    }

    @Override
    public String joinBoard(Integer boardId) {
        return "";
    }

    @Override
    public Integer getJoinedCount(Integer boardId) {
        return 0;
    }

    @Override
    public String getUserStatus(Integer boardId) {
        return "";
    }

    @Override
    public String getUserStatus1(Integer boardId) {
        return "";
    }

    @Override
    public String getUserStatus2(Integer boardId) {
        return "";
    }
}
