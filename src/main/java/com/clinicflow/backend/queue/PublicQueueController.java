package com.clinicflow.backend.queue;

import com.clinicflow.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/queue")
@RequiredArgsConstructor
public class PublicQueueController {

    private final QueueService queueService;

    @GetMapping("/{clinicId}/{tokenNumber}")
    public ApiResponse<PublicTokenStatusResponse> getTokenStatus(
            @PathVariable Long clinicId,
            @PathVariable Integer tokenNumber) {

        PublicTokenStatusResponse response = queueService.getPublicTokenStatus(clinicId, tokenNumber);

        return ApiResponse.success(
                "Token status fetched",
                response);
    }
}
