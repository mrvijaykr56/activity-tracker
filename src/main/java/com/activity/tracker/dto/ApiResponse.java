package com.activity.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private LocalDateTime timestamp;
    private int status;
    private String message;
    private T data;
    private Object errors;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(200)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, Object errors) {
        return ApiResponse.<T>builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .message(message)
                .errors(errors)
                .build();
    }
}
