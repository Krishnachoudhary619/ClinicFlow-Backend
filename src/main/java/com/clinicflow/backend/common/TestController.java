package com.clinicflow.backend.common;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/secure")
    public ApiResponse<String> secureEndpoint() {
        return ApiResponse.success("Access granted", "You are authenticated");
    }
}
