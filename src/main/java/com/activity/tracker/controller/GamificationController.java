package com.activity.tracker.controller;

import com.activity.tracker.dto.ApiResponse;
import com.activity.tracker.entities.User;
import com.activity.tracker.service.GamificationService;
import com.activity.tracker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/tracker/users/gamification")
public class GamificationController {

    private final GamificationService gamificationService;
    private final UserService userService;

    public GamificationController(GamificationService gamificationService, UserService userService) {
        this.gamificationService = gamificationService;
        this.userService = userService;
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

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getGamificationStatus() {
        User currentUser = getCurrentUser();
        log.info("Fetching gamification status for user: {}", currentUser.getUsername());
        
        Map<String, Object> status = gamificationService.getGamificationStatus(currentUser.getId());
        
        return ResponseEntity.ok(ApiResponse.success(status, "Gamification status retrieved successfully"));
    }
}
