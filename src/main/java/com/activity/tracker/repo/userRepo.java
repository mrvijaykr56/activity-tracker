package com.activity.tracker.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.activity.tracker.entities.User;


@Repository
public interface userRepo extends JpaRepository<User, Long> {
    User findByUsername(String userName);
}
