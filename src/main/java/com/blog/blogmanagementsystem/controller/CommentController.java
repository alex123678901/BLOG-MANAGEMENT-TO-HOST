package com.blog.blogmanagementsystem.controller;

import com.blog.blogmanagementsystem.model.Comment;
import com.blog.blogmanagementsystem.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{postId}")
    public ResponseEntity<com.blog.blogmanagementsystem.dto.CommentResponse> addComment(@PathVariable Long postId,
            @RequestParam Long userId,
            @RequestBody java.util.Map<String, Object> payload) {
        String content = (String) payload.get("content");
        Long parentId = null;
        if (payload.containsKey("parentId") && payload.get("parentId") != null) {
            parentId = Long.valueOf(payload.get("parentId").toString());
        }
        return new ResponseEntity<>(commentService.addComment(postId, userId, content, parentId), HttpStatus.CREATED);
    }

    @GetMapping("/post/{postId}")
    public ResponseEntity<List<com.blog.blogmanagementsystem.dto.CommentResponse>> getCommentsByPost(
            @PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsByPostId(postId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable Long id, @RequestParam Long userId) {
        try {
            commentService.deleteComment(id, userId);
            return ResponseEntity.ok("Comment deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
