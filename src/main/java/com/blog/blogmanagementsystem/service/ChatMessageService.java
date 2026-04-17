package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.model.ChatMessage;
import java.util.List;

public interface ChatMessageService {
    ChatMessage sendMessage(Long senderId, Long receiverId, String content);

    List<ChatMessage> getChatHistory(Long userId1, Long userId2);

    void markMessagesAsRead(Long senderId, Long receiverId);

    long getUnreadCount(Long userId);

    List<Long> getUnreadSenders(Long receiverId);

    void deleteMessage(Long id);
}
