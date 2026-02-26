package com.clinicflow.backend.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private ApiError error;

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> failure(String message, String code) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .error(new ApiError(code, null))
                .build();
    }
}
