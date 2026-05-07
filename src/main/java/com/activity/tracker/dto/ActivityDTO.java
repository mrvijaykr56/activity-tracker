package com.activity.tracker.dto;

import com.activity.tracker.entities.Category;
import com.activity.tracker.entities.Day;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityDTO {
    private Long id;

    @NotBlank(message = "Activity name is required")
    private String activityName;

    @NotNull(message = "Category is required")
    private Category category;

    @NotBlank(message = "Time duration is required")
    private String timeDuration;

    @NotBlank(message = "Date is required")
    private String date;

    @NotNull(message = "Days are required")
    private Day days;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
