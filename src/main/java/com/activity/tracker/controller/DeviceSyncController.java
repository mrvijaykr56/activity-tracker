package com.activity.tracker.controller;

import com.activity.tracker.dto.ApiResponse;
import com.activity.tracker.entities.Activity;
import com.activity.tracker.entities.Category;
import com.activity.tracker.entities.Day;
import com.activity.tracker.entities.User;
import com.activity.tracker.service.ActivityService;
import com.activity.tracker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Slf4j
@RestController
@RequestMapping("/tracker/users/sync")
public class DeviceSyncController {

    private final ActivityService activityService;
    private final UserService userService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DeviceSyncController(ActivityService activityService, UserService userService) {
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

    @PostMapping("/{device}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> syncDevice(@PathVariable String device) {
        User currentUser = getCurrentUser();
        log.info("Syncing device: {} for user: {}", device, currentUser.getUsername());

        // Simulate fetching data from wearable provider
        Random random = new Random();
        int activitiesCount = random.nextInt(3) + 1; // 1 to 3 activities
        
        for (int i = 0; i < activitiesCount; i++) {
            Activity syncActivity = Activity.builder()
                    .activityName("Synced " + (device.equals("Apple Health") ? "Walk" : "Run"))
                    .category(device.equals("Apple Health") ? Category.EXERCISE : Category.HOBBY)
                    .timeDuration((15 + random.nextInt(45)) + " mins")
                    .date(LocalDate.now().format(DATE_FORMATTER))
                    .days(Day.valueOf(LocalDate.now().getDayOfWeek().name()))
                    .user(currentUser)
                    .build();
            activityService.saveActivity(syncActivity);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("syncedActivities", activitiesCount);
        result.put("steps", 5000 + random.nextInt(5000));
        result.put("caloriesBurned", 200 + random.nextInt(300));
        result.put("lastSync", LocalDate.now().atStartOfDay().toString());

        return ResponseEntity.ok(ApiResponse.success(result, "Successfully synced with " + device));
    }
}
