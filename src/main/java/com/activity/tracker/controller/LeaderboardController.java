package com.activity.tracker.controller;

import com.activity.tracker.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/tracker/public/leaderboard")
public class LeaderboardController {

    private final com.activity.tracker.service.ActivityService activityService;

    public LeaderboardController(com.activity.tracker.service.ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getGlobalLeaderboard() {
        log.info("Fetching global leaderboard (Optimized)");
        List<Map<String, Object>> leaderboard = activityService.getLeaderboard();
        return ResponseEntity.ok(ApiResponse.success(leaderboard, "Leaderboard retrieved successfully"));
    }
}
