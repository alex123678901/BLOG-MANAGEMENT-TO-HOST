package com.blog.blogmanagementsystem.controller;

import com.blog.blogmanagementsystem.config.JwtUtils;
import com.blog.blogmanagementsystem.dto.JwtResponse;
import com.blog.blogmanagementsystem.dto.LoginRequest;
import com.blog.blogmanagementsystem.model.User;
import com.blog.blogmanagementsystem.service.ActivityLogService;
import com.blog.blogmanagementsystem.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

        private final AuthenticationManager authenticationManager;
        private final UserRepository userRepository;
        private final JwtUtils jwtUtils;
        private final ActivityLogService activityLogService;

        public AuthController(AuthenticationManager authenticationManager,
                        UserRepository userRepository,
                        JwtUtils jwtUtils,
                        ActivityLogService activityLogService) {
                this.authenticationManager = authenticationManager;
                this.userRepository = userRepository;
                this.jwtUtils = jwtUtils;
                this.activityLogService = activityLogService;
        }

        @PostMapping("/login")
        public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                                                loginRequest.getPassword()));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                String jwt = jwtUtils.generateJwtToken(authentication);

                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                List<String> roles = userDetails.getAuthorities().stream()
                                .map(item -> item.getAuthority())
                                .collect(Collectors.toList());

                User user = userRepository.findByUsername(userDetails.getUsername()).get();

                activityLogService.logActivity(user, "Logged in to the system");

                return ResponseEntity.ok(new JwtResponse(jwt,
                                user.getId(),
                                user.getUsername(),
                                user.getEmail(),
                                roles,
                                user.getFirstName(),
                                user.getLastName(),
                                user.getBio(),
                                user.getProfilePicture()));
        }
}
