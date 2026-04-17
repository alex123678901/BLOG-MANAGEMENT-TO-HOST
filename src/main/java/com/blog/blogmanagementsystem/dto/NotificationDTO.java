package com.blog.blogmanagementsystem.dto;

import com.blog.blogmanagementsystem.model.NotificationType;
import java.time.LocalDateTime;

public class NotificationDTO {

    private Long id;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;
    private String type;
    private Long targetId;

    public NotificationDTO() {
    }

    public NotificationDTO(Long id, String message, boolean isRead, LocalDateTime createdAt, String type,
            Long targetId) {
        this.id = id;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.type = type;
        this.targetId = targetId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}
