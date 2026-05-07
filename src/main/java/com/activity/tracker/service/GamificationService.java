package com.activity.tracker.service;

import com.activity.tracker.entities.Activity;
import com.activity.tracker.repo.ActivityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GamificationService {

    private final ActivityRepository activityRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public GamificationService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public int calculateCurrentStreak(Long userId) {
        List<String> dateStrings = activityRepository.findDistinctDatesByUserId(userId);
        
        if (dateStrings.isEmpty()) return 0;

        Set<LocalDate> activeDates = dateStrings.stream()
                .map(d -> LocalDate.parse(d, DATE_FORMATTER))
                .collect(Collectors.toSet());

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // If the latest activity is not today or yesterday, streak is 0
        if (!activeDates.contains(today) && !activeDates.contains(yesterday)) {
            return 0;
        }

        int streak = 0;
        LocalDate currentCheck = activeDates.contains(today) ? today : yesterday;

        while (activeDates.contains(currentCheck)) {
            streak++;
            currentCheck = currentCheck.minusDays(1);
        }

        return streak;
    }

    public List<Map<String, Object>> getEarnedBadges(Long userId) {
        List<Map<String, Object>> badges = new ArrayList<>();
        long totalActivities = activityRepository.countByUserId(userId);
        int currentStreak = calculateCurrentStreak(userId);

        if (totalActivities >= 1) {
            badges.add(createBadge("First Step", "Logged your first activity!", "bi-flag-fill", "success"));
        }
        if (totalActivities >= 10) {
            badges.add(createBadge("Tenacious", "Logged 10 activities.", "bi-award", "info"));
        }
        if (totalActivities >= 50) {
            badges.add(createBadge("Activity Master", "Logged 50 activities.", "bi-trophy-fill", "warning"));
        }
        if (currentStreak >= 3) {
            badges.add(createBadge("On Fire", "Achieved a 3-day streak!", "bi-fire", "danger"));
        }
        if (currentStreak >= 7) {
            badges.add(createBadge("Week Warrior", "Achieved a 7-day streak!", "bi-lightning-charge-fill", "primary"));
        }

        return badges;
    }

    public Map<String, Object> getGamificationStatus(Long userId) {
        Map<String, Object> status = new HashMap<>();
        int streak = calculateCurrentStreak(userId);
        long totalActivities = activityRepository.countByUserId(userId);
        
        status.put("streak", streak);
        status.put("badges", getEarnedBadges(userId));
        status.put("level", calculateLevel(totalActivities));
        status.put("totalActivities", totalActivities);
        
        return status;
    }

    private int calculateLevel(long activityCount) {
        if (activityCount < 5) return 1;
        if (activityCount < 20) return 2;
        if (activityCount < 50) return 3;
        if (activityCount < 100) return 4;
        return 5;
    }

    private Map<String, Object> createBadge(String name, String description, String icon, String color) {
        Map<String, Object> badge = new HashMap<>();
        badge.put("name", name);
        badge.put("description", description);
        badge.put("icon", icon);
        badge.put("color", color);
        return badge;
    }
}
