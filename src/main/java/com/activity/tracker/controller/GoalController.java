package com.activity.tracker.controller;

import com.activity.tracker.dto.ApiResponse;
import com.activity.tracker.dto.GoalDTO;
import com.activity.tracker.entities.Goal;
import com.activity.tracker.entities.User;
import com.activity.tracker.service.GoalService;
import com.activity.tracker.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/tracker/users/goals")
public class GoalController {

    private final GoalService goalService;
    private final UserService userService;

    public GoalController(GoalService goalService, UserService userService) {
        this.goalService = goalService;
        this.userService = userService;
    }

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userService.getUserByUsername(username);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalDTO>>> getGoals() {
        User currentUser = getCurrentUser();
        List<Goal> goals = goalService.getGoalsByUserId(currentUser.getId());
        List<GoalDTO> dtos = goals.stream().map(g -> GoalDTO.builder()
                .id(g.getId())
                .category(g.getCategory())
                .targetCount(g.getTargetCount())
                .currentCount(goalService.calculateCurrentProgress(currentUser.getId(), g.getCategory()))
                .build()
        ).collect(Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(dtos, "Goals retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GoalDTO>> setGoal(@RequestBody GoalDTO goalDto) {
        User currentUser = getCurrentUser();
        Goal goal = Goal.builder()
                .category(goalDto.getCategory())
                .targetCount(goalDto.getTargetCount())
                .build();
        
        Goal savedGoal = goalService.setGoal(currentUser, goal);
        GoalDTO responseDto = GoalDTO.builder()
                .id(savedGoal.getId())
                .category(savedGoal.getCategory())
                .targetCount(savedGoal.getTargetCount())
                .currentCount(goalService.calculateCurrentProgress(currentUser.getId(), savedGoal.getCategory()))
                .build();
                
        return ResponseEntity.ok(ApiResponse.success(responseDto, "Goal updated successfully"));
    }
}
