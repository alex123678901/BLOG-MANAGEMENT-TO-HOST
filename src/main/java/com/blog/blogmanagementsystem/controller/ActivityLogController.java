package com.blog.blogmanagementsystem.controller;

import com.blog.blogmanagementsystem.model.ActivityLog;
import com.blog.blogmanagementsystem.service.ActivityLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ActivityLogController {

    private final ActivityLogService activityLogService;

    public ActivityLogController(ActivityLogService activityLogService) {
        this.activityLogService = activityLogService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<ActivityLog>> getUserActivity(@PathVariable Long userId) {
        return ResponseEntity.ok(activityLogService.getUserActivity(userId));
    }
}
