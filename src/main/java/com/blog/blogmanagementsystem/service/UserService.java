package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.dto.DashboardStatsResponse;
import com.blog.blogmanagementsystem.dto.UserRequest;
import com.blog.blogmanagementsystem.dto.UserResponse;
import com.blog.blogmanagementsystem.model.User;
import java.util.List;

public interface UserService {
    UserResponse registerUser(UserRequest userRequest);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    List<UserResponse> getAllUsers();

    UserResponse updateProfile(Long id, UserRequest userRequest);

    UserResponse mapToResponse(User user);

    /**
     * Optimized variant: caller supplies the already-resolved current user ID to
     * skip the per-call DB lookup.
     */
    UserResponse mapToResponse(User user, Long currentUserId);

    void deleteUser(Long id);

    DashboardStatsResponse getDashboardStats();

    void toggleUserStatus(Long id);

    List<UserResponse> searchUsers(String query);

    List<UserResponse> getAllAuthors();

    void updateUserRoles(Long userId, java.util.Set<String> roles);

    UserResponse getUserByEmail(String email);
}
