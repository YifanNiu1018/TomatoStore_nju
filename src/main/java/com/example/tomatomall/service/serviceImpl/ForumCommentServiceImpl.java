package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.service.ForumCommentService;
import com.example.tomatomall.vo.ForumCommentVO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ForumCommentServiceImpl implements ForumCommentService {
    @Override
    public Object loadComment(String articleId, Integer pageNo, Integer orderType) {
        return null;
    }

    @Override
    public ForumCommentVO postComment(String articleId, Integer pCommentId, String content, String replyUserId, MultipartFile image) {
        return null;
    }

    @Override
    public String changeTopType(Integer commentId, Integer topType) {
        return "";
    }
}
