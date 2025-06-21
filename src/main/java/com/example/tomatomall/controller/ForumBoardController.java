package com.example.tomatomall.controller;

import com.example.tomatomall.service.ForumBoardService;
import com.example.tomatomall.vo.ForumBoardVO;
import com.example.tomatomall.vo.Response;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/board")
public class ForumBoardController {

    final
    ForumBoardService forumBoardService;

    public ForumBoardController(ForumBoardService forumBoardService) {
        this.forumBoardService = forumBoardService;
    }

    @RequestMapping("/loadBoard")
    public Response<List<ForumBoardVO>> loadBoard() {
        List<ForumBoardVO> forumBoardVOS = forumBoardService.loadBoard();
        return Response.buildSuccess(forumBoardVOS);
    }

    @RequestMapping("/createBoard")
    public Response<String> createBoard(ForumBoardVO forumBoardVO) {
        String result = forumBoardService.createBoard(forumBoardVO);
        return Response.buildSuccess(result);
    }

    @RequestMapping("/joinBoard")
    public Response<String> joinBoard(Integer boardId) {
        String result = forumBoardService.joinBoard(boardId);
        return Response.buildSuccess(result);
    }

    @RequestMapping("/getJoinedCount")
    public Response<Integer> getJoinedCount(Integer boardId) {
        Integer count = forumBoardService.getJoinedCount(boardId);
        return Response.buildSuccess(count);
    }

    @RequestMapping("/getUserStatus")
    public Response<String> getUserStatus(Integer boardId) {
        String status = forumBoardService.getUserStatus(boardId);
        return Response.buildSuccess(status);
    }

    @RequestMapping("/getUserStatus1")
    public Response<String> getUserStatus1(Integer boardId) {
        String status = forumBoardService.getUserStatus1(boardId);
        return Response.buildSuccess(status);
    }

    @RequestMapping("/getUserStatus2")
    public Response<String> getUserStatus2(Integer boardId) {
        String status = forumBoardService.getUserStatus2(boardId);
        return Response.buildSuccess(status);
    }
}
