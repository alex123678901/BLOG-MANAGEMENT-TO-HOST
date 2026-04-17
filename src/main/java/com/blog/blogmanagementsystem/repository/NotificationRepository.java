package com.blog.blogmanagementsystem.repository;

import com.blog.blogmanagementsystem.model.Notification;
import com.blog.blogmanagementsystem.model.NotificationType;
import com.blog.blogmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    Long countByUserIdAndIsReadFalse(Long userId);
    List<Notification> findByUserIdAndTypeAndTargetIdAndIsReadFalse(Long userId, NotificationType type, Long targetId);
}
