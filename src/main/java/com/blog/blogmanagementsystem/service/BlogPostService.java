package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.dto.PagedResponse;
import com.blog.blogmanagementsystem.dto.PostRequest;
import com.blog.blogmanagementsystem.dto.PostResponse;
import com.blog.blogmanagementsystem.model.PostStatus;
import org.springframework.data.domain.Pageable;

public interface BlogPostService {
    PostResponse createPost(PostRequest postRequest, Long authorId);

    PostResponse getPostById(Long id);

    PagedResponse<PostResponse> getAllPosts(Pageable pageable, PostStatus status, String search);

    PostResponse updatePost(Long id, PostRequest postRequest);

    void deletePost(Long id);

    PagedResponse<PostResponse> getPostsByAuthor(Long authorId, Pageable pageable);
}
