package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Comment;
import com.example.tomatomall.repository.CommentRepository;
import com.example.tomatomall.service.CommentService;
import com.example.tomatomall.vo.CommentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentVO addComment(CommentVO commentVO) {
        // 设置评论时间
        commentVO.setPostTime(new Date());
        // 初始化点赞数
        commentVO.setGoodCount(0);

        Comment comment = commentVO.toPO();
        comment = commentRepository.save(comment);

        return comment.toVO();
    }

    @Override
    @Transactional
    public CommentVO replyComment(CommentVO commentVO) {
        // 检查父评论是否存在
        Optional<Comment> parentComment = commentRepository.findById(commentVO.getPCommentId());
        if (!parentComment.isPresent()) {
            throw new RuntimeException("父评论不存在");
        }

        // 设置评论时间
        commentVO.setPostTime(new Date());
        // 初始化点赞数
        commentVO.setGoodCount(0);

        Comment comment = commentVO.toPO();
        comment = commentRepository.save(comment);

        return comment.toVO();
    }

    @Override
    public List<CommentVO> getProductComments(Integer productId) {
        List<Comment> comments = commentRepository.findByProductIdAndPCommentIdIsNull(productId);
        return comments.stream()
                .map(Comment::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentVO> getCommentReplies(Integer commentId) {
        List<Comment> replies = commentRepository.findByPCommentId(commentId);
        return replies.stream()
                .map(Comment::toVO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<CommentVO, List<CommentVO>> getAllProductComments(Integer productId) {
        // 获取一级评论
        List<Comment> primaryComments = commentRepository.findByProductIdAndPCommentIdIsNull(productId);
        Map<CommentVO, List<CommentVO>> result = new LinkedHashMap<>();

        // 处理每个一级评论及其回复
        for (Comment primaryComment : primaryComments) {
            CommentVO commentVO = primaryComment.toVO();
            List<Comment> replies = commentRepository.findByPCommentId(primaryComment.getCommentId());

            List<CommentVO> replyVOs = replies.stream()
                    .map(Comment::toVO)
                    .collect(Collectors.toList());

            result.put(commentVO, replyVOs);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean likeComment(Integer commentId) {
        int affected = commentRepository.incrementGoodCount(commentId);
        return affected > 0;
    }

    @Override
    @Transactional
    public boolean deleteComment(Integer commentId) {
        // 检查评论是否存在
        if (!commentRepository.existsById(commentId)) {
            return false;
        }

        // 先删除所有回复
        List<Comment> replies = commentRepository.findByPCommentId(commentId);
        if (!replies.isEmpty()) {
            commentRepository.deleteAll(replies);
        }

        // 删除评论本身
        commentRepository.deleteById(commentId);
        return true;
    }
}