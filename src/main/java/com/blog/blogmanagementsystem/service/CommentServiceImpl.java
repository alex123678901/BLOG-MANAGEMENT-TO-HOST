package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.exception.ResourceNotFoundException;
import com.blog.blogmanagementsystem.model.BlogPost;
import com.blog.blogmanagementsystem.model.Comment;
import com.blog.blogmanagementsystem.model.User;
import com.blog.blogmanagementsystem.repository.BlogPostRepository;
import com.blog.blogmanagementsystem.repository.CommentRepository;
import com.blog.blogmanagementsystem.repository.UserRepository;
import com.blog.blogmanagementsystem.service.ActivityLogService;
import com.blog.blogmanagementsystem.service.NotificationService;
import com.blog.blogmanagementsystem.model.NotificationType;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final ActivityLogService activityLogService;
    private final NotificationService notificationService;

    public CommentServiceImpl(CommentRepository commentRepository,
            BlogPostRepository blogPostRepository,
            UserRepository userRepository,
            ActivityLogService activityLogService,
            NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.blogPostRepository = blogPostRepository;
        this.userRepository = userRepository;
        this.activityLogService = activityLogService;
        this.notificationService = notificationService;
    }

    @Override
    public com.blog.blogmanagementsystem.dto.CommentResponse addComment(Long postId, Long userId, String content,
            Long parentId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", postId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setUser(user);

        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", parentId));

            // Per user request: only allow the owner of the post to reply to a comment.
            if (!post.getAuthor().getId().equals(user.getId())) {
                throw new RuntimeException("Only the author of the post can reply to comments.");
            }
            comment.setParent(parent);
        }

        Comment savedComment = commentRepository.save(comment);

        activityLogService.logActivity(user, "Commented on post: " + post.getTitle());

        // Notify original commenter if it's a reply
        if (parentId != null) {
            Comment parent = commentRepository.findById(parentId).get(); // Safely checked earlier
            // Only notify if they aren't somehow replying to themselves (though admins
            // could)
            if (!parent.getUser().getId().equals(user.getId())) {
                String message = user.getFirstName() + " " + user.getLastName() + " (@" + user.getUsername()
                        + ") replied to your comment on: " + post.getTitle();
                notificationService.createNotification(parent.getUser(), message, NotificationType.COMMENT,
                        post.getId());
            }
        }
        // Notify post author if it's a direct comment
        else if (!post.getAuthor().getId().equals(user.getId())) {
            String message = user.getFirstName() + " " + user.getLastName() + " (@" + user.getUsername()
                    + ") commented on your post: " + post.getTitle();
            notificationService.createNotification(post.getAuthor(), message, NotificationType.COMMENT, post.getId());
        }

        return mapToResponse(savedComment);
    }

    @Override
    public List<com.blog.blogmanagementsystem.dto.CommentResponse> getCommentsByPostId(Long postId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", postId));
        return commentRepository.findByPost(post).stream()
                .filter(c -> c.getParent() == null)
                .map(this::mapToResponse)
                .collect(java.util.stream.Collectors.toList());
    }

    private com.blog.blogmanagementsystem.dto.CommentResponse mapToResponse(Comment comment) {
        com.blog.blogmanagementsystem.dto.CommentResponse response = new com.blog.blogmanagementsystem.dto.CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setAuthorUsername(comment.getUser() != null ? comment.getUser().getUsername() : "Unknown");
        response.setAuthorId(comment.getUser() != null ? comment.getUser().getId() : -1L);
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            response.setReplies(comment.getReplies().stream().map(this::mapToResponse)
                    .collect(java.util.stream.Collectors.toList()));
        }
        return response;
    }

    @Override
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        boolean isCommentAuthor = comment.getUser().getId().equals(userId);
        boolean isPostAuthor = comment.getPost().getAuthor().getId().equals(userId);

        if (!isCommentAuthor && !isPostAuthor) {
            throw new RuntimeException("Unauthorized to delete this comment");
        }

        activityLogService.logActivity(comment.getUser(), "Deleted a comment");

        commentRepository.delete(comment);
    }

    @Override
    public long getCommentCount(Long postId) {
        BlogPost post = blogPostRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", postId));
        return commentRepository.findByPost(post).size();
    }
}
