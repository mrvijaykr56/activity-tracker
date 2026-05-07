package com.activity.tracker.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.activity.tracker.entities.Activity;
import com.activity.tracker.entities.Category;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Custom method to fetch activities by user ID with pagination
    Page<Activity> findByUserId(Long userId, Pageable pageable);

    // Method to search activities by user ID and activity name
    Page<Activity> findByUserIdAndActivityNameContainingIgnoreCase(Long userId, String activityName, Pageable pageable);

    @Query("SELECT a.category, COUNT(a) FROM Activity a WHERE a.user.id = :userId GROUP BY a.category")
    List<Object[]> countActivitiesByCategory(@Param("userId") Long userId);

    @Query("SELECT a.date, COUNT(a) FROM Activity a WHERE a.user.id = :userId GROUP BY a.date ORDER BY a.date DESC")
    List<Object[]> countActivitiesByDay(@Param("userId") Long userId);

    @Query("SELECT a.category, COUNT(a) FROM Activity a GROUP BY a.category")
    List<Object[]> countGlobalActivitiesByCategory();

    long countByUserId(Long userId);

    @Query("SELECT COUNT(a) FROM Activity a")
    long countTotalActivities();

    @Query("SELECT COUNT(a) FROM Activity a WHERE a.user.id = :userId AND a.category = :category AND a.date >= :startDate")
    long countByUserIdAndCategoryAndDateAfter(@Param("userId") Long userId, @Param("category") Category category, @Param("startDate") String startDate);

    @Query("SELECT u.username, COUNT(a) FROM User u JOIN Activity a ON u.id = a.user.id GROUP BY u.username ORDER BY COUNT(a) DESC")
    List<Object[]> getLeaderboardData(Pageable pageable);

    @Query("SELECT DISTINCT a.date FROM Activity a WHERE a.user.id = :userId")
    List<String> findDistinctDatesByUserId(@Param("userId") Long userId);
}
