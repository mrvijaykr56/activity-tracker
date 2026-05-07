package com.activity.tracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.activity.tracker.entities.Activity;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Custom method to fetch activities by user ID
    List<Activity> findByUserId(Long userId);
}
