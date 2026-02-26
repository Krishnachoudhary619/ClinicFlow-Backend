package com.clinicflow.backend;

import com.clinicflow.backend.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class RootController {

    @GetMapping("/")
    public ApiResponse<Map<String, String>> welcome() {
        return ApiResponse.success("Welcome to ClinicFlow API", Map.of(
                "version", "1.0.0",
                "status", "Running"));
    }
}
