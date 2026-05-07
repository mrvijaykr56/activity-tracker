package com.activity.tracker.controller;

import com.activity.tracker.dto.ApiResponse;
import com.activity.tracker.entities.Activity;
import com.activity.tracker.service.ActivityService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tracker/users/activity")
public class ActivityController {
    @Autowired
    private ActivityService activityService;

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Activity>> addActivity(@Valid @RequestBody Activity activity) {
        Activity savedActivity = activityService.saveActivity(activity);
        return ResponseEntity.ok(ApiResponse.success(savedActivity, "Activity added successfully"));
    }

    @PostMapping("/addAll")
    public ResponseEntity<ApiResponse<List<Activity>>> addAllActivities(@RequestBody List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            throw new IllegalArgumentException("Activities list cannot be empty.");
        }
        List<Activity> savedActivities = activityService.saveActivities(activities);
        return ResponseEntity.ok(ApiResponse.success(savedActivities, "All activities added successfully"));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Activity>>> getActivitiesByUserId(@PathVariable Long userId) {
        List<Activity> activities = activityService.getActivitiesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(activities, "Activities retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Activity>> updateActivity(@PathVariable Long id, @Valid @RequestBody Activity activity) {
        Activity updatedActivity = activityService.updateActivity(id, activity);
        return ResponseEntity.ok(ApiResponse.success(updatedActivity, "Activity updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Activity deleted successfully"));
    }
}