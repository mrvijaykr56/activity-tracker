package com.activity.tracker.dto;

import com.activity.tracker.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalDTO {
    private Long id;
    private Category category;
    private Integer targetCount;
    private Integer currentCount; // Calculated progress
}
