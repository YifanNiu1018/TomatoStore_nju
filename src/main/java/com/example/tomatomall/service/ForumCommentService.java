package com.example.tomatomall.service;

import com.example.tomatomall.vo.ForumCommentVO;
import org.springframework.web.multipart.MultipartFile;

public interface ForumCommentService {
    Object loadComment(String articleId, Integer pageNo, Integer orderType);

    ForumCommentVO postComment(String articleId, Integer pCommentId, String content, String replyUserId, MultipartFile image);

    String changeTopType(Integer commentId, Integer topType);
}
