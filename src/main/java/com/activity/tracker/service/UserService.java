package com.activity.tracker.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.activity.tracker.entities.User;
import com.activity.tracker.repo.userRepo;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private userRepo userRepository;
    
    // Method to authenticate the user
    public boolean authenticate(String userName, String password) {
        // Fetch the user from the database by userName
        User user = userRepository.findByUsername(userName);
        // Validate credentials: Check if the user exists and if the password matches
        return user != null && user.getPassword().equals(password);
    }
    
 // Method to fetch a user by username
    public User getUserByUsername(String username) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            return user; // Return the user if found
        } else {
            throw new RuntimeException("User not found with username: " + username);
        }
    }

    // Add a user
    public User addUser(User user) {
        return userRepository.save(user);
    }

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update a user
    public User updateUser(Long id, User updatedUser) {
        return userRepository.findById(id).map(user -> {
        	user.setAge(updatedUser.getAge());
        	user.setFirstname(updatedUser.getFirstname());
            user.setLastname(updatedUser.getLastname());
            user.setUsername(updatedUser.getUsername());
            user.setPassword(updatedUser.getPassword());
            return userRepository.save(user);
        }).orElseThrow(() -> new RuntimeException("User not found with id " + id));
    }

    // Delete a user
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}