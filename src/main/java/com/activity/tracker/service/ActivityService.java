package com.activity.tracker.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.activity.tracker.entities.Activity;
import com.activity.tracker.repo.ActivityRepository;

import java.util.List;

@Service
public class ActivityService {
    
    private final ActivityRepository activityRepository;

    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    // Save a single activity
    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    // Save a list of activities
    public List<Activity> saveActivities(List<Activity> activities) {
        return activityRepository.saveAll(activities);
    }

    // Retrieve all activities
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }
    
    // Retrieve all activities by passing user id with pagination
    public Page<Activity> getActivitiesByUserId(Long userId, Pageable pageable) {
        return activityRepository.findByUserId(userId, pageable);
    }

    // Delete an activity
    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    // Update an activity
    public Activity updateActivity(Long id, Activity activityDetails) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));
        
        activity.setActivityName(activityDetails.getActivityName());
        activity.setCategory(activityDetails.getCategory());
        activity.setTimeDuration(activityDetails.getTimeDuration());
        activity.setDate(activityDetails.getDate());
        activity.setDays(activityDetails.getDays());
        
        return activityRepository.save(activity);
    }
}
