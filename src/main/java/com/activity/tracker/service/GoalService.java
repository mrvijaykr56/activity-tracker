package com.activity.tracker.service;

import com.activity.tracker.entities.Activity;
import com.activity.tracker.entities.Goal;
import com.activity.tracker.entities.User;
import com.activity.tracker.repo.ActivityRepository;
import com.activity.tracker.repo.GoalRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final ActivityRepository activityRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public GoalService(GoalRepository goalRepository, ActivityRepository activityRepository) {
        this.goalRepository = goalRepository;
        this.activityRepository = activityRepository;
    }

    public List<Goal> getGoalsByUserId(Long userId) {
        return goalRepository.findByUserId(userId);
    }

    public Goal setGoal(User user, Goal goalDetails) {
        Goal goal = goalRepository.findByUserIdAndCategory(user.getId(), goalDetails.getCategory())
                .orElse(Goal.builder().user(user).category(goalDetails.getCategory()).build());
        
        goal.setTargetCount(goalDetails.getTargetCount());
        return goalRepository.save(goal);
    }

    public int calculateCurrentProgress(Long userId, com.activity.tracker.entities.Category category) {
        LocalDate startOfWeek = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        String startDateStr = startOfWeek.format(DATE_FORMATTER);
        
        return (int) activityRepository.countByUserIdAndCategoryAndDateAfter(userId, category, startDateStr);
    }
}
