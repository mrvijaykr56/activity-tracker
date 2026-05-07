package com.activity.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.activity.tracker.entities.Activity;
import com.activity.tracker.repo.ActivityRepository;

import java.util.List;

@Service
public class ActivityService {
    @Autowired
    private ActivityRepository activityRepository;

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
    
    // Retrieve all activities by passing user id
    public List<Activity> getActivitiesByUserId(Long userId) {
        return activityRepository.findByUserId(userId);
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
