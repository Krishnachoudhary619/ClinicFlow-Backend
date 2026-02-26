package com.clinicflow.backend.queue;

import com.clinicflow.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicQueueController {

    private final QueueService queueService;

    @GetMapping("/token/{tokenId}")
    public ApiResponse<PublicTokenStatusResponse> getTokenStatus(
            @PathVariable Long tokenId) {

        PublicTokenStatusResponse response = queueService.getPublicTokenStatus(tokenId);

        return ApiResponse.success(
                "Token status fetched",
                response);
    }
}
