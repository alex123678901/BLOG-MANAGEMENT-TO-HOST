package com.blog.blogmanagementsystem.controller;

import com.blog.blogmanagementsystem.dto.PagedResponse;
import com.blog.blogmanagementsystem.dto.PostRequest;
import com.blog.blogmanagementsystem.dto.PostResponse;
import com.blog.blogmanagementsystem.model.PostStatus;
import com.blog.blogmanagementsystem.service.BlogPostService;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/posts")
public class BlogPostController {

    private final BlogPostService blogPostService;

    public BlogPostController(BlogPostService blogPostService) {
        this.blogPostService = blogPostService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@Valid @RequestBody PostRequest postRequest,
            @RequestParam Long authorId) {
        return new ResponseEntity<>(blogPostService.createPost(postRequest, authorId), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        return ResponseEntity.ok(blogPostService.getPostById(id));
    }

    @GetMapping
    public ResponseEntity<PagedResponse<PostResponse>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir,
            @RequestParam(value = "status", required = false) PostStatus status,
            @RequestParam(value = "search", required = false) String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        return ResponseEntity.ok(blogPostService.getAllPosts(pageable, status, search));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Long id,
            @Valid @RequestBody PostRequest postRequest) {
        return ResponseEntity.ok(blogPostService.updatePost(id, postRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable Long id) {
        blogPostService.deletePost(id);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<PagedResponse<PostResponse>> getPostsByAuthor(
            @PathVariable Long authorId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(blogPostService.getPostsByAuthor(authorId, pageable));
    }
}
