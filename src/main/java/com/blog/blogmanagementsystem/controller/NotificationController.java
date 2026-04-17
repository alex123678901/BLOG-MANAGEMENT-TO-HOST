package com.blog.blogmanagementsystem.controller;

import com.blog.blogmanagementsystem.dto.NotificationDTO;
import com.blog.blogmanagementsystem.model.NotificationType;
import com.blog.blogmanagementsystem.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<NotificationDTO>> getUserNotifications(@PathVariable Long userId) {
        List<NotificationDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/{userId}/unread-count")
    public ResponseEntity<Long> getUnreadCount(@PathVariable Long userId) {
        Long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/mark-read")
    public ResponseEntity<?> markReadByTypeAndTarget(
            @RequestParam Long userId,
            @RequestParam NotificationType type,
            @RequestParam Long targetId) {
        notificationService.markByTypeAndTargetAsRead(userId, type, targetId);
        return ResponseEntity.ok().build();
    }
}
