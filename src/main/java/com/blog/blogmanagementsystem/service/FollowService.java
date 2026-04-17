package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.model.User;
import java.util.List;

public interface FollowService {
    void followUser(Long followerId, Long followedId);

    void unfollowUser(Long followerId, Long followedId);

    boolean isFollowing(Long followerId, Long followedId);

    List<User> getFollowers(Long userId);

    List<User> getFollowing(Long userId);

    long getFollowerCount(Long userId);

    long getFollowingCount(Long userId);
}
