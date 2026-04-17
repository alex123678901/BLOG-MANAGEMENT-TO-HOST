package com.blog.blogmanagementsystem.repository;

import com.blog.blogmanagementsystem.model.ChatMessage;
import com.blog.blogmanagementsystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderAndReceiverOrSenderAndReceiverOrderByTimestampAsc(
            User sender1, User receiver1, User sender2, User receiver2);

    List<ChatMessage> findBySenderAndReceiverAndIsReadFalse(User sender, User receiver);

    long countByReceiverAndIsReadFalse(User receiver);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT m.sender.id FROM ChatMessage m WHERE m.receiver = :receiver AND m.isRead = false")
    List<Long> findDistinctSendersWithUnreadMessages(
            @org.springframework.data.repository.query.Param("receiver") User receiver);
}
