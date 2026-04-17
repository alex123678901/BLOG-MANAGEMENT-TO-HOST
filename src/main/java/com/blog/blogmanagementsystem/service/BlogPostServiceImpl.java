package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.dto.PagedResponse;
import com.blog.blogmanagementsystem.dto.PostRequest;
import com.blog.blogmanagementsystem.dto.PostResponse;
import com.blog.blogmanagementsystem.exception.ResourceNotFoundException;
import com.blog.blogmanagementsystem.model.BlogPost;
import com.blog.blogmanagementsystem.model.Category;
import com.blog.blogmanagementsystem.model.PostStatus;
import com.blog.blogmanagementsystem.model.User;
import com.blog.blogmanagementsystem.repository.BlogPostRepository;
import com.blog.blogmanagementsystem.repository.BlogPostSpecification;
import com.blog.blogmanagementsystem.repository.CategoryRepository;
import com.blog.blogmanagementsystem.repository.UserRepository;
import com.blog.blogmanagementsystem.service.ActivityLogService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BlogPostServiceImpl implements BlogPostService {

    private final BlogPostRepository blogPostRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ActivityLogService activityLogService;
    private final UserService userService;
    private final LikeService likeService;
    private final CommentService commentService;

    public BlogPostServiceImpl(BlogPostRepository blogPostRepository,
            UserRepository userRepository,
            CategoryRepository categoryRepository,
            ActivityLogService activityLogService,
            UserService userService,
            LikeService likeService,
            CommentService commentService) {
        this.blogPostRepository = blogPostRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.activityLogService = activityLogService;
        this.userService = userService;
        this.likeService = likeService;
        this.commentService = commentService;
    }

    @Override
    public PostResponse createPost(PostRequest postRequest, Long authorId) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));

        BlogPost post = new BlogPost();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setSummary(postRequest.getSummary());
        post.setFeaturedImage(postRequest.getFeaturedImage());
        post.setAuthor(author);
        post.setStatus(postRequest.getStatus() != null ? postRequest.getStatus() : PostStatus.DRAFT);
        post.setMediaType(postRequest.getMediaType() != null ? postRequest.getMediaType()
                : com.blog.blogmanagementsystem.model.MediaType.BLOG);

        if (postRequest.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(postRequest.getCategoryIds()));
            post.setCategories(categories);
        }

        BlogPost savedPost = blogPostRepository.save(post);

        activityLogService.logActivity(author, "Created a new post: " + savedPost.getTitle());

        return mapToResponse(savedPost);
    }

    @Override
    public PostResponse getPostById(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));
        // Increment view count
        post.setViewCount(post.getViewCount() + 1);
        blogPostRepository.save(post);
        return mapToResponse(post);
    }

    @Override
    public PagedResponse<PostResponse> getAllPosts(Pageable pageable, PostStatus status, String search) {
        Page<BlogPost> posts = blogPostRepository.findAll(BlogPostSpecification.filterPosts(status, search), pageable);
        return mapToPagedResponse(posts);
    }

    @Override
    public PostResponse updatePost(Long id, PostRequest postRequest) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));

        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setSummary(postRequest.getSummary());
        post.setFeaturedImage(postRequest.getFeaturedImage());
        if (postRequest.getMediaType() != null) {
            post.setMediaType(postRequest.getMediaType());
        }

        if (postRequest.getCategoryIds() != null) {
            Set<Category> categories = new HashSet<>(categoryRepository.findAllById(postRequest.getCategoryIds()));
            post.setCategories(categories);
        }

        BlogPost updatedPost = blogPostRepository.save(post);

        activityLogService.logActivity(post.getAuthor(), "Updated post: " + updatedPost.getTitle());

        return mapToResponse(updatedPost);
    }

    @Override
    public void deletePost(Long id) {
        BlogPost post = blogPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BlogPost", "id", id));

        activityLogService.logActivity(post.getAuthor(), "Deleted post: " + post.getTitle());

        blogPostRepository.delete(post);
    }

    @Override
    public PagedResponse<PostResponse> getPostsByAuthor(Long authorId, Pageable pageable) {
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", authorId));
        Page<BlogPost> posts = blogPostRepository.findByAuthor(author, pageable);
        return mapToPagedResponse(posts);
    }

    private PagedResponse<PostResponse> mapToPagedResponse(Page<BlogPost> posts) {
        // Resolve the current user's ID once — avoids N DB lookups for follow-status
        // checks
        Long currentUserId = null;
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String username = null;
                if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                    username = ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal())
                            .getUsername();
                } else if (auth.getPrincipal() instanceof String) {
                    username = (String) auth.getPrincipal();
                }
                if (username != null) {
                    currentUserId = userRepository.findByUsername(username)
                            .map(u -> u.getId()).orElse(null);
                }
            }
        } catch (Exception ignored) {
        }

        final Long resolvedUserId = currentUserId;
        List<PostResponse> content = posts.getContent().stream()
                .map(post -> mapToResponse(post, resolvedUserId))
                .collect(Collectors.toList());

        return new PagedResponse<>(
                content,
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalElements(),
                posts.getTotalPages(),
                posts.isLast());
    }

    private PostResponse mapToResponse(BlogPost post) {
        return mapToResponse(post, null);
    }

    private PostResponse mapToResponse(BlogPost post, Long currentUserId) {
        PostResponse response = new PostResponse();
        response.setId(post.getId());
        response.setTitle(post.getTitle());
        response.setContent(post.getContent());
        response.setSummary(post.getSummary());
        response.setFeaturedImage(post.getFeaturedImage());
        response.setAuthor(userService.mapToResponse(post.getAuthor(), currentUserId));
        response.setStatus(post.getStatus().name());
        response.setViewCount(post.getViewCount());
        response.setLikeCount(likeService.getLikeCount(post.getId()));
        response.setCommentCount(commentService.getCommentCount(post.getId()));
        response.setLikedByCurrentUser(currentUserId != null && likeService.isPostLikedByUser(post.getId(), currentUserId));
        response.setCreatedAt(post.getCreatedAt());
        response.setUpdatedAt(post.getUpdatedAt());
        response.setCategories(post.getCategories().stream()
                .map(Category::getName)
                .collect(Collectors.toSet()));
        return response;
    }
}
