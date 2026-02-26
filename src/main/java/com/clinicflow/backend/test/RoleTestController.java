package com.clinicflow.backend.test;

import com.clinicflow.backend.common.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/role-test")
public class RoleTestController {

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<String> adminOnly() {
        return ApiResponse.success("Admin access granted", "You are ADMIN");
    }

    @GetMapping("/doctor")
    @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<String> doctorOnly() {
        return ApiResponse.success("Doctor access granted", "You are DOCTOR");
    }

    @GetMapping("/reception")
    @PreAuthorize("hasRole('RECEPTIONIST')")
    public ApiResponse<String> receptionistOnly() {
        return ApiResponse.success("Reception access granted", "You are RECEPTIONIST");
    }

    @GetMapping("/admin-or-reception")
    @PreAuthorize("hasAnyRole('ADMIN','RECEPTIONIST')")
    public ApiResponse<String> adminOrReception() {
        return ApiResponse.success("Access granted", "Admin or Reception");
    }
}
