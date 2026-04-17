package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.model.ActivityLog;
import com.blog.blogmanagementsystem.model.User;

import java.util.List;

public interface ActivityLogService {
    void logActivity(User user, String action);

    void logActivity(Long userId, String action);

    List<ActivityLog> getUserActivity(Long userId);
}
