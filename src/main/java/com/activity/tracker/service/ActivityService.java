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

    // Retrieve single activity
    public Activity getActivityById(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Activity not found with id: " + id));
    }
    
    // Retrieve all activities by passing user id with pagination and optional search
    public Page<Activity> getActivitiesByUserId(Long userId, String search, Pageable pageable) {
        if (search != null && !search.trim().isEmpty()) {
            return activityRepository.findByUserIdAndActivityNameContainingIgnoreCase(userId, search, pageable);
        }
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

    public java.util.Map<String, Long> getCategoryDistributionByUserId(Long userId) {
        java.util.List<Object[]> results = activityRepository.countActivitiesByCategory(userId);
        java.util.Map<String, Long> distribution = new java.util.HashMap<>();
        for (Object[] result : results) {
            distribution.put(result[0].toString(), (Long) result[1]);
        }
        return distribution;
    }

    public java.util.Map<String, Long> getDailyDistributionByUserId(Long userId) {
        java.util.List<Object[]> results = activityRepository.countActivitiesByDay(userId);
        java.util.Map<String, Long> distribution = new java.util.LinkedHashMap<>();
        int count = 0;
        for (Object[] result : results) {
            if (count >= 7) break;
            distribution.put(result[0].toString(), (Long) result[1]);
            count++;
        }
        return distribution;
    }

    public java.util.Map<String, Object> getUserActivitySummary(Long userId) {
        java.util.Map<String, Object> summary = new java.util.HashMap<>();
        summary.put("totalActivities", activityRepository.countByUserId(userId));
        summary.put("categoryDistribution", getCategoryDistributionByUserId(userId));
        summary.put("dailyDistribution", getDailyDistributionByUserId(userId));
        return summary;
    }

    public java.util.Map<String, Long> getGlobalCategoryDistribution() {
        java.util.List<Object[]> results = activityRepository.countGlobalActivitiesByCategory();
        java.util.Map<String, Long> distribution = new java.util.HashMap<>();
        for (Object[] result : results) {
            distribution.put(result[0].toString(), (Long) result[1]);
        }
        return distribution;
    }

    public java.util.Map<String, Object> getCommunityStats() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalGlobalActivities", activityRepository.countTotalActivities());
        stats.put("globalCategoryDistribution", getGlobalCategoryDistribution());
        return stats;
    }
    public java.util.List<java.util.Map<String, Object>> getLeaderboard() {
        org.springframework.data.domain.Pageable topTen = org.springframework.data.domain.PageRequest.of(0, 10);
        java.util.List<Object[]> results = activityRepository.getLeaderboardData(topTen);
        
        java.util.List<java.util.Map<String, Object>> leaderboard = new java.util.ArrayList<>();
        for (Object[] result : results) {
            java.util.Map<String, Object> entry = new java.util.HashMap<>();
            long count = (Long) result[1];
            entry.put("username", result[0].toString());
            entry.put("activityCount", count);
            entry.put("level", calculateLevel(count));
            leaderboard.add(entry);
        }
        return leaderboard;
    }

    private int calculateLevel(long activityCount) {
        if (activityCount < 5) return 1;
        if (activityCount < 20) return 2;
        if (activityCount < 50) return 3;
        if (activityCount < 100) return 4;
        return 5;
    }
}
