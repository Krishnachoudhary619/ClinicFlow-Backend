package com.clinicflow.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodayAnalyticsResponse {

    private Long totalGenerated;
    private Long totalServed;
    private Long totalSkipped;
    private Double avgWaitMinutes;
}
