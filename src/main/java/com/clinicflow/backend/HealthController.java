package com.clinicflow.backend;

import com.clinicflow.backend.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @GetMapping
    public ApiResponse<Map<String, Object>> healthCheck() {
        return ApiResponse.success("ClinicFlow Backend is running", Map.of(
                "status", "UP",
                "timestamp", LocalDateTime.now()));
    }
}
