package com.clinicflow.backend.analytics.controller;

import com.clinicflow.backend.analytics.dto.HistoryAnalyticsResponse;
import com.clinicflow.backend.analytics.dto.HourlyAnalyticsResponse;
import com.clinicflow.backend.analytics.dto.TodayAnalyticsResponse;
import com.clinicflow.backend.analytics.service.AnalyticsService;
import com.clinicflow.backend.auth.UserPrincipal;
import com.clinicflow.backend.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
@Slf4j
public class AnalyticsController {

        private final AnalyticsService analyticsService;

        @GetMapping("/today")
        public ApiResponse<TodayAnalyticsResponse> today(
                        @AuthenticationPrincipal UserPrincipal user) {
                try {
                        Long clinicId = user.getClinicId();
                        log.info("Request: Today analytics for clinic {}", clinicId);
                        TodayAnalyticsResponse response = analyticsService.getTodayAnalytics(clinicId);
                        log.info("Success: Today analytics response generated");
                        return ApiResponse.success("Today analytics fetched", response);
                } catch (Exception e) {
                        log.error("CRITICAL: Today analytics failed", e);
                        throw e;
                }
        }

        @GetMapping("/hourly")
        public ApiResponse<List<HourlyAnalyticsResponse>> hourly(
                        @RequestParam LocalDate date) {
                try {
                        return ApiResponse.success(
                                        "Hourly analytics fetched",
                                        analyticsService.getHourlyAnalytics(date));
                } catch (Exception e) {
                        log.error("Error fetching hourly analytics: ", e);
                        throw e;
                }
        }

        @GetMapping("/history")
        public ApiResponse<List<HistoryAnalyticsResponse>> history(
                        @RequestParam(defaultValue = "7") int days) {
                return ApiResponse.success(
                                "History analytics fetched",
                                analyticsService.getHistoryAnalytics(days));
        }
}
