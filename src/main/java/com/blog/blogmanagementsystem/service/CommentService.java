package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.model.Comment;
import com.blog.blogmanagementsystem.dto.CommentResponse;
import java.util.List;

public interface CommentService {
    CommentResponse addComment(Long postId, Long userId, String content, Long parentId);

    List<CommentResponse> getCommentsByPostId(Long postId);

    void deleteComment(Long commentId, Long userId);

    long getCommentCount(Long postId);
}
