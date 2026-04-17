package com.blog.blogmanagementsystem.service;

public interface LikeService {
    void likePost(Long postId, Long userId);

    void unlikePost(Long postId, Long userId);

    Long getLikeCount(Long postId);

    boolean isPostLikedByUser(Long postId, Long userId);
}
