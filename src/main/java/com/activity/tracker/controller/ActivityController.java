package com.activity.tracker.controller;

import com.activity.tracker.dto.ApiResponse;
import com.activity.tracker.entities.Activity;
import com.activity.tracker.entities.User;
import com.activity.tracker.service.ActivityService;
import com.activity.tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/tracker/users/activity")
public class ActivityController {
    
    private final ActivityService activityService;
    private final UserService userService;

    public ActivityController(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
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

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<Activity>> addActivity(@Valid @RequestBody Activity activity) {
        User currentUser = getCurrentUser();
        log.info("Adding activity for user: {}", currentUser.getUsername());
        activity.setUser(currentUser);
        Activity savedActivity = activityService.saveActivity(activity);
        return ResponseEntity.ok(ApiResponse.success(savedActivity, "Activity added successfully"));
    }

    @PostMapping("/addAll")
    public ResponseEntity<ApiResponse<List<Activity>>> addAllActivities(@RequestBody List<Activity> activities) {
        if (activities == null || activities.isEmpty()) {
            throw new IllegalArgumentException("Activities list cannot be empty.");
        }
        User currentUser = getCurrentUser();
        log.info("Adding {} activities for user: {}", activities.size(), currentUser.getUsername());
        activities.forEach(a -> a.setUser(currentUser));
        List<Activity> savedActivities = activityService.saveActivities(activities);
        return ResponseEntity.ok(ApiResponse.success(savedActivities, "All activities added successfully"));
    }

    @GetMapping("/my-activities")
    public ResponseEntity<ApiResponse<Page<Activity>>> getMyActivities(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {
        
        User currentUser = getCurrentUser();
        log.info("Fetching activities for user: {}, search: {}", currentUser.getUsername(), search);
        
        Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
        
        Page<Activity> activities = activityService.getActivitiesByUserId(currentUser.getId(), search, pageable);
        return ResponseEntity.ok(ApiResponse.success(activities, "Activities retrieved successfully"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Activity>> updateActivity(@PathVariable Long id, @Valid @RequestBody Activity activity) {
        User currentUser = getCurrentUser();
        log.info("Updating activity {} for user: {}", id, currentUser.getUsername());
        // Verify ownership (Security measure)
        Activity existingActivity = activityService.getActivityById(id);
        if (!existingActivity.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized update attempt for activity {} by user {}", id, currentUser.getUsername());
            throw new RuntimeException("Unauthorized to update this activity");
        }
        
        Activity updatedActivity = activityService.updateActivity(id, activity);
        return ResponseEntity.ok(ApiResponse.success(updatedActivity, "Activity updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable Long id) {
        User currentUser = getCurrentUser();
        log.info("Deleting activity {} for user: {}", id, currentUser.getUsername());
        // Verify ownership
        Activity existingActivity = activityService.getActivityById(id);
        if (!existingActivity.getUser().getId().equals(currentUser.getId())) {
            log.warn("Unauthorized delete attempt for activity {} by user {}", id, currentUser.getUsername());
            throw new RuntimeException("Unauthorized to delete this activity");
        }
        
        activityService.deleteActivity(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Activity deleted successfully"));
    }
}