package com.activity.tracker.service;

import com.activity.tracker.entities.Activity;
import com.activity.tracker.entities.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

    private final ActivityService activityService;
    private final UserService userService;

    public SecurityService(ActivityService activityService, UserService userService) {
        this.activityService = activityService;
        this.userService = userService;
    }

    public boolean isActivityOwner(Long activityId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }

        User currentUser = userService.getUserByUsername(username);
        Activity activity = activityService.getActivityById(activityId);
        
        return activity.getUser().getId().equals(currentUser.getId());
    }
}
