package com.activity.tracker.controller;

import com.activity.tracker.dto.ActivityDTO;
import com.activity.tracker.dto.ApiResponse;
import com.activity.tracker.entities.Activity;
import com.activity.tracker.entities.User;
import com.activity.tracker.mapper.ActivityMapper;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
 
 import java.util.List;
 
 @Slf4j
 @RestController
 @RequestMapping("/tracker/users/activity")
 public class ActivityController {
     
     private final ActivityService activityService;
     private final UserService userService;
     private final ActivityMapper activityMapper;
 
     public ActivityController(ActivityService activityService, UserService userService, ActivityMapper activityMapper) {
         this.activityService = activityService;
         this.userService = userService;
         this.activityMapper = activityMapper;
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
     public ResponseEntity<ApiResponse<ActivityDTO>> addActivity(@Valid @RequestBody ActivityDTO activityDto) {
         User currentUser = getCurrentUser();
         log.info("Adding activity for user: {}", currentUser.getUsername());
         
         Activity activity = activityMapper.toEntity(activityDto);
         activity.setUser(currentUser);
         
         Activity savedActivity = activityService.saveActivity(activity);
         return ResponseEntity.ok(ApiResponse.success(activityMapper.toDto(savedActivity), "Activity added successfully"));
     }
 
     @PostMapping("/addAll")
     public ResponseEntity<ApiResponse<List<ActivityDTO>>> addAllActivities(@RequestBody List<ActivityDTO> activityDtos) {
         if (activityDtos == null || activityDtos.isEmpty()) {
             throw new IllegalArgumentException("Activities list cannot be empty.");
         }
         User currentUser = getCurrentUser();
         log.info("Adding {} activities for user: {}", activityDtos.size(), currentUser.getUsername());
         
         List<Activity> activities = activityMapper.toEntityList(activityDtos);
         activities.forEach(a -> a.setUser(currentUser));
         
         List<Activity> savedActivities = activityService.saveActivities(activities);
         return ResponseEntity.ok(ApiResponse.success(activityMapper.toDtoList(savedActivities), "All activities added successfully"));
     }
 
     @GetMapping("/my-activities")
     public ResponseEntity<ApiResponse<Page<ActivityDTO>>> getMyActivities(
             @RequestParam(required = false) String search,
             @RequestParam(defaultValue = "0") int page,
             @RequestParam(defaultValue = "5") int size,
             @RequestParam(defaultValue = "id,desc") String[] sort) {
         
         User currentUser = getCurrentUser();
         log.info("Fetching activities for user: {}, search: {}", currentUser.getUsername(), search);
         
         Sort.Direction direction = sort[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
         Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort[0]));
         
         Page<Activity> activities = activityService.getActivitiesByUserId(currentUser.getId(), search, pageable);
         Page<ActivityDTO> activityDtos = activities.map(activityMapper::toDto);
         
         return ResponseEntity.ok(ApiResponse.success(activityDtos, "Activities retrieved successfully"));
     }
 
     @PutMapping("/{id}")
     @PreAuthorize("@securityService.isActivityOwner(#id)")
     public ResponseEntity<ApiResponse<ActivityDTO>> updateActivity(@PathVariable Long id, @Valid @RequestBody ActivityDTO activityDto) {
         log.info("Updating activity {} for current user", id);
         
         Activity activityToUpdate = activityMapper.toEntity(activityDto);
         Activity updatedActivity = activityService.updateActivity(id, activityToUpdate);
         
         return ResponseEntity.ok(ApiResponse.success(activityMapper.toDto(updatedActivity), "Activity updated successfully"));
     }
 
     @DeleteMapping("/{id}")
     @PreAuthorize("@securityService.isActivityOwner(#id)")
     public ResponseEntity<ApiResponse<Void>> deleteActivity(@PathVariable Long id) {
         log.info("Deleting activity {} for current user", id);
         
         activityService.deleteActivity(id);
         return ResponseEntity.ok(ApiResponse.success(null, "Activity deleted successfully"));
     }
 }