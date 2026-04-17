package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.exception.ResourceNotFoundException;
import com.blog.blogmanagementsystem.model.ChatMessage;
import com.blog.blogmanagementsystem.model.User;
import com.blog.blogmanagementsystem.repository.ChatMessageRepository;
import com.blog.blogmanagementsystem.repository.UserRepository;
import com.blog.blogmanagementsystem.model.NotificationType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

        private final ChatMessageRepository chatMessageRepository;
        private final UserRepository userRepository;
        private final SimpMessagingTemplate messagingTemplate;
        private final FollowService followService;
        private final NotificationService notificationService;

        public ChatMessageServiceImpl(ChatMessageRepository chatMessageRepository,
                        UserRepository userRepository,
                        SimpMessagingTemplate messagingTemplate,
                        FollowService followService,
                        NotificationService notificationService) {
                this.chatMessageRepository = chatMessageRepository;
                this.userRepository = userRepository;
                this.messagingTemplate = messagingTemplate;
                this.followService = followService;
                this.notificationService = notificationService;
        }

        @Override
        public ChatMessage sendMessage(Long senderId, Long receiverId, String content) {
                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));
                User receiver = userRepository.findById(receiverId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", receiverId));

                // Check for mutual follow
                if (!followService.isFollowing(senderId, receiverId)
                                || !followService.isFollowing(receiverId, senderId)) {
                        throw new RuntimeException("You can only chat with users who mutually follow you.");
                }

                ChatMessage message = new ChatMessage();
                message.setSender(sender);
                message.setReceiver(receiver);
                message.setContent(content);

                ChatMessage savedMessage = chatMessageRepository.save(message);

                // Broadcast to receiver
                messagingTemplate.convertAndSendToUser(
                                receiver.getUsername(),
                                "/queue/messages",
                                savedMessage);

                // Notify receiver
                String notifMessage = "New message from " + sender.getFirstName() + " " + sender.getLastName() + " (@"
                                + sender.getUsername() + ")";
                notificationService.createNotification(receiver, notifMessage, NotificationType.MESSAGE, senderId);

                return savedMessage;
        }

        @Override
        public List<ChatMessage> getChatHistory(Long userId1, Long userId2) {
                User user1 = userRepository.findById(userId1)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId1));
                User user2 = userRepository.findById(userId2)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId2));

                return chatMessageRepository.findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(
                                user1, user2, user2, user1);
        }

        @Override
        public void markMessagesAsRead(Long senderId, Long receiverId) {
                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", senderId));
                User receiver = userRepository.findById(receiverId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", receiverId));

                List<ChatMessage> unreadMessages = chatMessageRepository.findBySenderAndReceiverAndIsReadFalse(sender,
                                receiver);
                for (ChatMessage message : unreadMessages) {
                        message.setRead(true);
                }
                chatMessageRepository.saveAll(unreadMessages);
        }

        @Override
        public long getUnreadCount(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                return chatMessageRepository.countByReceiverAndIsReadFalse(user);
        }

        @Override
        public List<Long> getUnreadSenders(Long receiverId) {
                User receiver = userRepository.findById(receiverId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", receiverId));
                return chatMessageRepository.findDistinctSendersWithUnreadMessages(receiver);
        }

        @Override
        public void deleteMessage(Long id) {
                if (!chatMessageRepository.existsById(id)) {
                        throw new ResourceNotFoundException("ChatMessage", "id", id);
                }
                chatMessageRepository.deleteById(id);
        }
}
