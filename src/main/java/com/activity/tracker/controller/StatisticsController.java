package com.activity.tracker.controller;

import com.activity.tracker.dto.ApiResponse;
import com.activity.tracker.entities.User;
import com.activity.tracker.service.ActivityService;
import com.activity.tracker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/tracker/users/stats")
public class StatisticsController {

    private final ActivityService activityService;
    private final UserService userService;
    private final com.activity.tracker.service.AiService aiService;

    public StatisticsController(ActivityService activityService, UserService userService, com.activity.tracker.service.AiService aiService) {
        this.activityService = activityService;
        this.userService = userService;
        this.aiService = aiService;
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userService.getUserByUsername(username);
    }

    @GetMapping("/category-distribution")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCategoryDistribution() {
        User currentUser = getCurrentUser();
        log.info("Fetching category distribution for user: {}", currentUser.getUsername());
        Map<String, Long> stats = activityService.getCategoryDistributionByUserId(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(stats, "Category distribution retrieved successfully"));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGeneralSummary() {
        User currentUser = getCurrentUser();
        log.info("Fetching summary for user: {}", currentUser.getUsername());
        Map<String, Object> summary = activityService.getUserActivitySummary(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(summary, "User activity summary retrieved successfully"));
    }

    @GetMapping("/wellness-tips")
    public ResponseEntity<ApiResponse<List<String>>> getWellnessTips() {
        User currentUser = getCurrentUser();
        log.info("Fetching wellness tips for user: {}", currentUser.getUsername());
        List<String> tips = aiService.generateWellnessTips(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(tips, "Wellness tips retrieved successfully"));
    }
}
