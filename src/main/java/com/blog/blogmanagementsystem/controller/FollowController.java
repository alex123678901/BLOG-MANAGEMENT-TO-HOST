package com.blog.blogmanagementsystem.controller;

import com.blog.blogmanagementsystem.model.User;
import com.blog.blogmanagementsystem.service.FollowService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
public class FollowController {

    private final FollowService followService;

    private final com.blog.blogmanagementsystem.service.UserService userService;

    public FollowController(FollowService followService, com.blog.blogmanagementsystem.service.UserService userService) {
        this.followService = followService;
        this.userService = userService;
    }

    @PostMapping("/{followedId}")
    public ResponseEntity<?> followUser(@PathVariable Long followedId, @RequestParam Long followerId) {
        try {
            followService.followUser(followerId, followedId);
            return ResponseEntity.ok(Map.of("message", "Successfully followed user"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{followedId}")
    public ResponseEntity<?> unfollowUser(@PathVariable Long followedId, @RequestParam Long followerId) {
        try {
            followService.unfollowUser(followerId, followedId);
            return ResponseEntity.ok(Map.of("message", "Successfully unfollowed user"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/status/{followedId}")
    public ResponseEntity<?> getFollowStatus(@PathVariable Long followedId, @RequestParam Long followerId) {
        boolean isFollowing = followService.isFollowing(followerId, followedId);
        return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<?> getFollowStats(@PathVariable Long userId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("followersCount", followService.getFollowerCount(userId));
        stats.put("followingCount", followService.getFollowingCount(userId));
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<com.blog.blogmanagementsystem.dto.UserResponse>> getFollowers(@PathVariable Long userId) {
        List<com.blog.blogmanagementsystem.dto.UserResponse> responses = followService.getFollowers(userId).stream()
                .map(userService::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<com.blog.blogmanagementsystem.dto.UserResponse>> getFollowing(@PathVariable Long userId) {
        List<com.blog.blogmanagementsystem.dto.UserResponse> responses = followService.getFollowing(userId).stream()
                .map(userService::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
