package com.activity.tracker.controller;

import com.activity.tracker.dto.ApiResponse;
import com.activity.tracker.service.ActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/tracker/public/community")
public class CommunityController {

    private final ActivityService activityService;

    public CommunityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/global-distribution")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getGlobalCategoryDistribution() {
        log.info("Fetching global category distribution");
        Map<String, Long> stats = activityService.getGlobalCategoryDistribution();
        return ResponseEntity.ok(ApiResponse.success(stats, "Global distribution retrieved successfully"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCommunityStats() {
        log.info("Fetching community stats");
        Map<String, Object> stats = activityService.getCommunityStats();
        return ResponseEntity.ok(ApiResponse.success(stats, "Community stats retrieved successfully"));
    }
}
