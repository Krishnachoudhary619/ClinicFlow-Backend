package com.clinicflow.backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Object> handleApiException(ApiException ex) {
        return ApiResponse.failure(ex.getMessage(), "400");
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<Object> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex) {
        return ApiResponse.failure("Access denied", "403");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Object> handleGeneral(Exception ex) {
        return ApiResponse.failure("Something went wrong", "500");
    }
}
