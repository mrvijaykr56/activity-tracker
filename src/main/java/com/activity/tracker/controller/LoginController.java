package com.activity.tracker.controller;

import com.activity.tracker.config.JwtUtils;
import com.activity.tracker.dto.ApiResponse;
import com.activity.tracker.dto.LoginRequest;
import com.activity.tracker.dto.UserDTO;
import com.activity.tracker.entities.User;
import com.activity.tracker.exception.ResourceNotFoundException;
import com.activity.tracker.mapper.UserMapper;
import com.activity.tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tracker/users")
public class LoginController {
 
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
 
    public LoginController(UserService userService, JwtUtils jwtUtils, UserMapper userMapper) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
    }
 
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@Valid @RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        
        if (isAuthenticated) {
            User authenticatedUser = userService.getUserByUsername(loginRequest.getUsername());
            String jwt = jwtUtils.generateToken(authenticatedUser.getUsername());
            
            Map<String, Object> data = new HashMap<>();
            data.put("user", userMapper.toDto(authenticatedUser));
            data.put("token", jwt);
            return ResponseEntity.ok(ApiResponse.success(data, "Login Successful"));
        } else {
            throw new IllegalArgumentException("Invalid credentials");
        }
    }
 
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> addUser(@Valid @RequestBody User user) {
        User savedUser = userService.addUser(user);
        return ResponseEntity.ok(ApiResponse.success(userMapper.toDto(savedUser), "Registration Successful"));
    }
 
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserDTO> userDtos = users.stream().map(userMapper::toDto).toList();
        return ResponseEntity.ok(ApiResponse.success(userDtos, "Users retrieved successfully"));
    }
 
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id " + id));
        return ResponseEntity.ok(ApiResponse.success(userMapper.toDto(user), "User retrieved successfully"));
    }
 
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @RequestBody User user) {
        if (!userService.getUserById(id).isPresent()) {
            throw new ResourceNotFoundException("Cannot update. User not found with id " + id);
        }
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(ApiResponse.success(userMapper.toDto(updatedUser), "User updated successfully"));
    }
 
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        if (!userService.getUserById(id).isPresent()) {
            throw new ResourceNotFoundException("Cannot delete. User not found with id " + id);
        }
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted successfully"));
    }
}