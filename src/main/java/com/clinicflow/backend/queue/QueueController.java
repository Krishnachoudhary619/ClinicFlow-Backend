package com.clinicflow.backend.queue;

import com.clinicflow.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/queue")
@RequiredArgsConstructor
public class QueueController {

    private final QueueService queueService;

    @PostMapping("/token")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','ADMIN')")
    public ApiResponse<Token> generateToken(
            @RequestBody CreateTokenRequest request) {

        Token token = queueService.createToken(request);

        return ApiResponse.success(
                "Token generated successfully",
                token);
    }

    @GetMapping("/current")
    @PreAuthorize("hasAnyRole('RECEPTIONIST','DOCTOR','ADMIN')")
    public ApiResponse<QueueResponse> getQueue() {

        QueueResponse response = queueService.getCurrentQueue();

        return ApiResponse.success(
                "Queue fetched successfully",
                response);
    }

    @PostMapping("/serve-next")
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST','ADMIN')")
    public ApiResponse<QueueResponse> serveNext() {

        QueueResponse response = queueService.markCurrentAsServed();

        return ApiResponse.success(
                "Token served and next called",
                response);
    }
}
