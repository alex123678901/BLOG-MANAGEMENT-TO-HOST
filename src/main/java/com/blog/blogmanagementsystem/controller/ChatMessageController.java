package com.blog.blogmanagementsystem.controller;

import com.blog.blogmanagementsystem.model.ChatMessage;
import com.blog.blogmanagementsystem.service.ChatMessageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;

    public ChatMessageController(ChatMessageService chatMessageService) {
        this.chatMessageService = chatMessageService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String content) {
        return new ResponseEntity<>(chatMessageService.sendMessage(senderId, receiverId, content), HttpStatus.CREATED);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@RequestParam Long user1Id,
            @RequestParam Long user2Id) {
        return ResponseEntity.ok(chatMessageService.getChatHistory(user1Id, user2Id));
    }

    @PutMapping("/read")
    public ResponseEntity<Void> markAsRead(@RequestParam Long senderId, @RequestParam Long receiverId) {
        chatMessageService.markMessagesAsRead(senderId, receiverId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@RequestParam Long userId) {
        return ResponseEntity.ok(chatMessageService.getUnreadCount(userId));
    }

    @GetMapping("/unread-senders")
    public ResponseEntity<List<Long>> getUnreadSenders(@RequestParam Long userId) {
        return ResponseEntity.ok(chatMessageService.getUnreadSenders(userId));
    }

    @DeleteMapping("/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMessage(@PathVariable Long id) {
        chatMessageService.deleteMessage(id);
        return ResponseEntity.ok("Message deleted successfully");
    }
}
