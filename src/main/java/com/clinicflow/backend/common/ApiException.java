package com.clinicflow.backend.common;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }

    public ApiException(String message, String code) {
        super(message);
    }
}
