package com.activity.tracker.service;

import com.activity.tracker.entities.Activity;
import com.activity.tracker.entities.Category;
import com.activity.tracker.repo.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final ActivityRepository activityRepository;
    private final Random random = new Random();

    public AiService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    public List<String> generateWellnessTips(Long userId) {
        List<Object[]> categoryCountsRaw = activityRepository.countActivitiesByCategory(userId);
        Map<Category, Long> categoryCounts = categoryCountsRaw.stream()
                .collect(Collectors.toMap(
                    row -> (Category) row[0],
                    row -> (Long) row[1]
                ));

        List<String> tips = new ArrayList<>();

        if (categoryCounts.isEmpty()) {
            tips.add("Welcome! Start by logging your first activity to get personalized wellness insights.");
            return tips;
        }

        // Logic-based tips
        if (categoryCounts.getOrDefault(Category.WORK, 0L) > 5 && categoryCounts.getOrDefault(Category.EXERCISE, 0L) == 0) {
            tips.add("You've been working hard! Consider adding a short exercise session to boost your energy levels.");
        }

        if (categoryCounts.getOrDefault(Category.EXERCISE, 0L) > 3) {
            tips.add("Great consistency with your workouts! Remember to stay hydrated and prioritize recovery.");
        }

        if (categoryCounts.getOrDefault(Category.HOBBY, 0L) < 2) {
            tips.add("Don't forget to make time for your hobbies. They are essential for a balanced and happy life.");
        }

        // Add some variety/random tips if list is short
        String[] generalTips = {
            "Consistency is key to building long-term habits.",
            "Try to log your activities as soon as you complete them for better accuracy.",
            "Celebrate your small wins! Every logged activity is progress.",
            "A 10-minute walk can significantly improve your focus and mood."
        };

        while (tips.size() < 3) {
            String randomTip = generalTips[random.nextInt(generalTips.length)];
            if (!tips.contains(randomTip)) {
                tips.add(randomTip);
            }
        }

        return tips;
    }
}
