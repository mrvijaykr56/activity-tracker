package com.activity.tracker.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    
    @Min(value = 1)
    @Max(value = 120)
    private Long age;
    
    @NotBlank
    private String firstname;
    
    @NotBlank
    private String lastname;
    
    @NotBlank
    private String username;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
