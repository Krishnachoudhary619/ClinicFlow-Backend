package com.clinicflow.backend.setup;

import com.clinicflow.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/setup")
@RequiredArgsConstructor
public class SetupController {

    private final SetupService setupService;

    @PostMapping("/create-clinic")
    public ApiResponse<String> createClinic(@RequestBody CreateClinicRequest request) {

        String message = setupService.createClinic(request);

        return ApiResponse.success(message, null);
    }
}
