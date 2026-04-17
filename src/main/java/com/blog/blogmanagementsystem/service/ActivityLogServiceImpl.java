package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.model.ActivityLog;
import com.blog.blogmanagementsystem.model.User;
import com.blog.blogmanagementsystem.repository.ActivityLogRepository;
import com.blog.blogmanagementsystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepository activityLogRepository;
    private final UserRepository userRepository;

    public ActivityLogServiceImpl(ActivityLogRepository activityLogRepository, UserRepository userRepository) {
        this.activityLogRepository = activityLogRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void logActivity(User user, String action) {
        if (user != null) {
            ActivityLog log = new ActivityLog(user, action);
            activityLogRepository.save(log);
        }
    }

    @Override
    public void logActivity(Long userId, String action) {
        userRepository.findById(userId).ifPresent(user -> logActivity(user, action));
    }

    @Override
    public List<ActivityLog> getUserActivity(Long userId) {
        return activityLogRepository.findByUserIdOrderByTimestampDesc(userId);
    }
}
