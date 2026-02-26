package com.clinicflow.backend.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PublicTokenStatusResponse {

    private Integer tokenNumber;
    private String status;
    private Integer currentServing;
    private Integer patientsAhead;
    private Integer estimatedWaitMinutes;
}
