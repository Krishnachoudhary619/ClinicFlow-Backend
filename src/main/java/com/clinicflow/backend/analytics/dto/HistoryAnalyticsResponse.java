package com.clinicflow.backend.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class HistoryAnalyticsResponse {

    private LocalDate date;
    private Long generated;
    private Long served;
}
