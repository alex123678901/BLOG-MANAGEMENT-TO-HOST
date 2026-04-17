package com.blog.blogmanagementsystem.service;

import java.util.List;

public interface NotificationService {
    void createNotification(com.blog.blogmanagementsystem.model.User recipient, String message, com.blog.blogmanagementsystem.model.NotificationType type, Long targetId);

    List<com.blog.blogmanagementsystem.dto.NotificationDTO> getUserNotifications(Long userId);

    void markAsRead(Long notificationId);

    void markByTypeAndTargetAsRead(Long userId, com.blog.blogmanagementsystem.model.NotificationType type, Long targetId);

    Long getUnreadCount(Long userId);
}
