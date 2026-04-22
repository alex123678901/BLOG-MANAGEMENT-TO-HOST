-- Database Fix Script for Blog Management System
-- This script adds ON DELETE CASCADE to foreign keys that might be missing it

USE blog_management_db;

-- Fix activity_logs foreign key
ALTER TABLE activity_logs DROP FOREIGN KEY FK5bm1lt4f4eevt8lv2517soakd;
ALTER TABLE activity_logs ADD CONSTRAINT FK5bm1lt4f4eevt8lv2517soakd 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Fix any other potentially missing cascades (based on typical issues)
-- Notifications
ALTER TABLE notifications DROP FOREIGN KEY IF EXISTS notifications_ibfk_1;
ALTER TABLE notifications ADD CONSTRAINT fk_notif_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Blog Posts
ALTER TABLE blog_posts DROP FOREIGN KEY IF EXISTS blog_posts_ibfk_1;
ALTER TABLE blog_posts ADD CONSTRAINT fk_post_author 
    FOREIGN KEY (author_id) REFERENCES users(id) ON DELETE CASCADE;

-- Comments
ALTER TABLE comments DROP FOREIGN KEY IF EXISTS comments_ibfk_1;
ALTER TABLE comments ADD CONSTRAINT fk_comment_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Likes
ALTER TABLE likes DROP FOREIGN KEY IF EXISTS likes_ibfk_1;
ALTER TABLE likes ADD CONSTRAINT fk_like_user 
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE;

-- Follows
ALTER TABLE follows DROP FOREIGN KEY IF EXISTS follows_ibfk_1;
ALTER TABLE follows ADD CONSTRAINT fk_follow_follower 
    FOREIGN KEY (follower_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE follows DROP FOREIGN KEY IF EXISTS follows_ibfk_2;
ALTER TABLE follows ADD CONSTRAINT fk_follow_followed 
    FOREIGN KEY (followed_id) REFERENCES users(id) ON DELETE CASCADE;

-- Chat Messages
ALTER TABLE chat_messages DROP FOREIGN KEY IF EXISTS chat_messages_ibfk_1;
ALTER TABLE chat_messages ADD CONSTRAINT fk_chat_sender 
    FOREIGN KEY (sender_id) REFERENCES users(id) ON DELETE CASCADE;

ALTER TABLE chat_messages DROP FOREIGN KEY IF EXISTS chat_messages_ibfk_2;
ALTER TABLE chat_messages ADD CONSTRAINT fk_chat_receiver 
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE;
