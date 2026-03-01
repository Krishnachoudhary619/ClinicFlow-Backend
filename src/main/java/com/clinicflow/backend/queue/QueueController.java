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
    public ApiResponse<TokenResponse> generateToken(
            @RequestBody CreateTokenRequest request) {

        Token token = queueService.createToken(request);

        TokenResponse response = TokenResponse.builder()
                .tokenId(token.getId())
                .tokenNumber(token.getTokenNumber())
                .cycleNumber(token.getCycleNumber())
                .build();

        return ApiResponse.success(
                "Token generated successfully",
                response);
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

        QueueServeResult result = queueService.markCurrentAsServed();

        return ApiResponse.success(
                result.getMessage(),
                result.getQueue());
    }

    @PostMapping("/skip")
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST','ADMIN')")
    public ApiResponse<QueueResponse> skipToken() {

        QueueResponse response = queueService.skipCurrentToken();

        return ApiResponse.success(
                "Token skipped successfully",
                response);
    }

    @PostMapping("/reset")
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST','ADMIN')")
    public ApiResponse<String> resetTokens() {

        String message = queueService.resetTokens();

        return ApiResponse.success(message, null);
    }

    @PostMapping("/start-new-day")
    @PreAuthorize("hasAnyRole('DOCTOR','RECEPTIONIST','ADMIN')")
    public ApiResponse<String> startNewDay() {

        String message = queueService.startNewDay();

        return ApiResponse.success(message, null);
    }
}
