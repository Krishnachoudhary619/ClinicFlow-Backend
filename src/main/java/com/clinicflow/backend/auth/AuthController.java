package com.clinicflow.backend.auth;

import com.clinicflow.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody LoginRequest request) {
        return ApiResponse.success("Login successful", authService.login(request.getEmail(), request.getPassword()));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ApiResponse.success("Token refreshed", authService.refresh(request.getRefreshToken()));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(
            @AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ApiResponse.failure("User not authenticated", "AUTH_NOT_AUTHENTICATED");
        }

        UserResponse response = UserResponse.builder()
                .id(principal.getUserId())
                .email(principal.getUsername())
                .role(principal.getRole())
                .clinicId(principal.getClinicId())
                .build();

        return ApiResponse.success("User fetched", response);
    }
}
