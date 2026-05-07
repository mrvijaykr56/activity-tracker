package com.activity.tracker.repo;

import com.activity.tracker.entities.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import com.activity.tracker.entities.Category;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByUserId(Long userId);
    Optional<Goal> findByUserIdAndCategory(Long userId, Category category);
}
