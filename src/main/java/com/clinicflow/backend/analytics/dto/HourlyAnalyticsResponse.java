package com.clinicflow.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HourlyAnalyticsResponse {

    private Integer hour;
    private Long count;
}
