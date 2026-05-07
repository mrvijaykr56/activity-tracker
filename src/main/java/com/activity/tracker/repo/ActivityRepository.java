package com.activity.tracker.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.activity.tracker.entities.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    // Custom method to fetch activities by user ID with pagination
    Page<Activity> findByUserId(Long userId, Pageable pageable);
}
