package com.blog.blogmanagementsystem.service;

import com.blog.blogmanagementsystem.dto.DashboardStatsResponse;
import com.blog.blogmanagementsystem.dto.UserRequest;
import com.blog.blogmanagementsystem.dto.UserResponse;
import com.blog.blogmanagementsystem.exception.ResourceNotFoundException;
import com.blog.blogmanagementsystem.model.User;
import com.blog.blogmanagementsystem.model.Role;
import com.blog.blogmanagementsystem.repository.BlogPostRepository;
import com.blog.blogmanagementsystem.repository.CommentRepository;
import com.blog.blogmanagementsystem.repository.RoleRepository;
import com.blog.blogmanagementsystem.repository.UserRepository;
import com.blog.blogmanagementsystem.service.ActivityLogService;
import com.blog.blogmanagementsystem.service.FollowService;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BlogPostRepository blogPostRepository;
    private final CommentRepository commentRepository;
    private final RoleRepository roleRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;
    private final ActivityLogService activityLogService;
    private final FollowService followService;

    public UserServiceImpl(UserRepository userRepository,
            BlogPostRepository blogPostRepository,
            CommentRepository commentRepository,
            RoleRepository roleRepository,
            org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
            ActivityLogService activityLogService,
            FollowService followService) {
        this.userRepository = userRepository;
        this.blogPostRepository = blogPostRepository;
        this.commentRepository = commentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.activityLogService = activityLogService;
        this.followService = followService;
    }

    @Override
    public UserResponse registerUser(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setBio(userRequest.getBio());
        user.setProfilePicture(userRequest.getProfilePicture());
        user.setActive(true);

        Set<Role> roles = new HashSet<>();
        // Always add ROLE_READER for everyone
        Role readerRole = roleRepository.findByName("ROLE_READER")
                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_READER is not found."));
        roles.add(readerRole);

        if (userRequest.getRoles() != null && !userRequest.getRoles().isEmpty()) {
            for (String roleName : userRequest.getRoles()) {
                if (!roleName.equals("ROLE_READER")) { // Avoid duplicate if already added
                    Role role = roleRepository.findByName(roleName)
                            .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " is not found."));
                    roles.add(role);
                }
            }
        }
        user.setRoles(roles);

        User savedUser = userRepository.save(user);

        activityLogService.logActivity(savedUser, "Registered a new account");

        return mapToResponse(savedUser);
    }

    @Override
    public void toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        user.setActive(!user.isActive());
        userRepository.save(user);

        String action = user.isActive() ? "Account enabled by admin" : "Account disabled by admin";
        activityLogService.logActivity(user, action);
    }

    @Override
    public List<UserResponse> getAllAuthors() {
        // Return all active, non-admin users so anyone can be followed/found in friends
        // list
        return userRepository.findAll().stream()
                .filter(User::isActive)
                .filter(user -> user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN")))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToResponse(user);
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse updateProfile(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setBio(userRequest.getBio());
        user.setProfilePicture(userRequest.getProfilePicture());

        if (userRequest.getPassword() != null && !userRequest.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }

        User updatedUser = userRepository.save(user);

        activityLogService.logActivity(updatedUser, "Updated profile information");

        return mapToResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        userRepository.delete(user);
    }

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalPosts = blogPostRepository.count();
        long totalUsers = userRepository.count();
        long totalComments = commentRepository.count();
        Long totalViews = blogPostRepository.sumViewCount();

        return new DashboardStatsResponse(
                totalPosts,
                totalUsers,
                totalComments,
                totalViews != null ? totalViews : 0L);
    }

    @Override
    public List<UserResponse> searchUsers(String query) {
        return userRepository.searchUsers(query).stream()
                .filter(user -> user.getRoles().stream().noneMatch(role -> role.getName().equals("ROLE_ADMIN")))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse mapToResponse(User user) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setBio(user.getBio());
        response.setProfilePicture(user.getProfilePicture());
        response.setActive(user.isActive());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));

        // Populate follow flags if context exists
        try {
            org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                String currentUsername = null;
                if (auth.getPrincipal() instanceof org.springframework.security.core.userdetails.UserDetails) {
                    currentUsername = ((org.springframework.security.core.userdetails.UserDetails) auth.getPrincipal())
                            .getUsername();
                } else if (auth.getPrincipal() instanceof String) {
                    currentUsername = (String) auth.getPrincipal();
                }

                if (currentUsername != null && !currentUsername.equals(user.getUsername())) {
                    userRepository.findByUsername(currentUsername).ifPresent(currentUser -> {
                        response.setFollowing(followService.isFollowing(currentUser.getId(), user.getId()));
                        response.setFollowedBy(followService.isFollowing(user.getId(), currentUser.getId()));
                    });
                }
            }
        } catch (Exception e) {
            // Context might not be available in all cases
        }

        return response;
    }

    @Override
    public UserResponse mapToResponse(User user, Long currentUserId) {
        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setBio(user.getBio());
        response.setProfilePicture(user.getProfilePicture());
        response.setActive(user.isActive());
        response.setRoles(user.getRoles().stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet()));

        // Use the pre-resolved currentUserId — no extra DB lookup needed
        if (currentUserId != null && !currentUserId.equals(user.getId())) {
            response.setFollowing(followService.isFollowing(currentUserId, user.getId()));
            response.setFollowedBy(followService.isFollowing(user.getId(), currentUserId));
        }

        return response;
    }

    @Override
    public void updateUserRoles(Long userId, Set<String> roles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Set<Role> rolesToSet = new HashSet<>();
        for (String roleName : roles) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Error: Role " + roleName + " is not found."));
            rolesToSet.add(role);
        }
        user.setRoles(rolesToSet);
        userRepository.save(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return mapToResponse(user);
    }
}
