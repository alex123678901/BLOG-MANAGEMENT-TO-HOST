package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.exception.ResourceNotFoundException;
import com.blog.blogmanagementsystem.model.BlogPost;
import com.blog.blogmanagementsystem.model.Like;
import com.blog.blogmanagementsystem.model.User;
import com.blog.blogmanagementsystem.repository.BlogPostRepository;
import com.blog.blogmanagementsystem.repository.LikeRepository;
import com.blog.blogmanagementsystem.repository.UserRepository;
import com.blog.blogmanagementsystem.model.NotificationType;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

        private final LikeRepository likeRepository;
        private final BlogPostRepository blogPostRepository;
        private final UserRepository userRepository;
        private final NotificationService notificationService;

        public LikeServiceImpl(LikeRepository likeRepository,
                        BlogPostRepository blogPostRepository,
                        UserRepository userRepository,
                        NotificationService notificationService) {
                this.likeRepository = likeRepository;
                this.blogPostRepository = blogPostRepository;
                this.userRepository = userRepository;
                this.notificationService = notificationService;
        }

        @Override
        public void likePost(Long postId, Long userId) {
                BlogPost post = blogPostRepository.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", postId));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

                if (!isPostLikedByUser(postId, userId)) {
                        Like like = new Like();
                        like.setPost(post);
                        like.setUser(user);
                        likeRepository.save(like);

                        // Notify post author
                        if (!post.getAuthor().getId().equals(user.getId())) {
                                String message = user.getFirstName() + " " + user.getLastName() + " (@"
                                                + user.getUsername()
                                                + ") liked your post: " + post.getTitle();
                                notificationService.createNotification(post.getAuthor(), message, NotificationType.LIKE,
                                                post.getId());
                        }
                }
        }

        @Override
        public void unlikePost(Long postId, Long userId) {
                BlogPost post = blogPostRepository.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", postId));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

                likeRepository.findByUserAndPost(user, post).ifPresent(likeRepository::delete);
        }

        @Override
        public Long getLikeCount(Long postId) {
                BlogPost post = blogPostRepository.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", postId));
                return likeRepository.countByPost(post);
        }

        @Override
        public boolean isPostLikedByUser(Long postId, Long userId) {
                BlogPost post = blogPostRepository.findById(postId)
                                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", postId));
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
                return likeRepository.findByUserAndPost(user, post).isPresent();
        }
}
