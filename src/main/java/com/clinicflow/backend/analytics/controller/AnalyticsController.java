package com.clinicflow.backend.analytics.controller;

import com.clinicflow.backend.analytics.dto.HistoryAnalyticsResponse;
import com.clinicflow.backend.analytics.dto.HourlyAnalyticsResponse;
import com.clinicflow.backend.analytics.dto.TodayAnalyticsResponse;
import com.clinicflow.backend.analytics.service.AnalyticsService;
import com.clinicflow.backend.auth.UserPrincipal;
import com.clinicflow.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/today")
    public ApiResponse<TodayAnalyticsResponse> today(
            @AuthenticationPrincipal UserPrincipal user) {
        return ApiResponse.success(
                "Today analytics fetched",
                analyticsService.getTodayAnalytics(user.getClinicId()));
    }

    @GetMapping("/hourly")
    public ApiResponse<List<HourlyAnalyticsResponse>> hourly(
            @RequestParam LocalDate date) {
        return ApiResponse.success(
                "Hourly analytics fetched",
                analyticsService.getHourlyAnalytics(date));
    }

    @GetMapping("/history")
    public ApiResponse<List<HistoryAnalyticsResponse>> history(
            @RequestParam(defaultValue = "7") int days) {
        return ApiResponse.success(
                "History analytics fetched",
                analyticsService.getHistoryAnalytics(days));
    }
}
