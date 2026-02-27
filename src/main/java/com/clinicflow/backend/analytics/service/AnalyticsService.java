package com.clinicflow.backend.analytics.service;

import com.clinicflow.backend.analytics.dto.HistoryAnalyticsResponse;
import com.clinicflow.backend.analytics.dto.HourlyAnalyticsResponse;
import com.clinicflow.backend.analytics.dto.TodayAnalyticsResponse;
import com.clinicflow.backend.queue.ClinicDay;
import com.clinicflow.backend.queue.ClinicDayService;
import com.clinicflow.backend.queue.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

        private final TokenRepository tokenRepository;
        private final ClinicDayService clinicDayService;

        public TodayAnalyticsResponse getTodayAnalytics(Long clinicId) {

                ClinicDay today = clinicDayService.getActiveClinicDay(clinicId);

                Object[] result = (Object[]) tokenRepository.getTodayAnalytics(today.getId());

                Long totalGenerated = ((Number) result[0]).longValue();
                Long totalServed = result[1] != null ? ((Number) result[1]).longValue() : 0L;
                Long totalSkipped = result[2] != null ? ((Number) result[2]).longValue() : 0L;
                Double avgWait = result[3] != null ? ((Number) result[3]).doubleValue() : 0.0;

                return new TodayAnalyticsResponse(
                                totalGenerated,
                                totalServed,
                                totalSkipped,
                                avgWait);
        }

        public List<HourlyAnalyticsResponse> getHourlyAnalytics(LocalDate date) {

                return tokenRepository.getHourlyAnalytics(date)
                                .stream()
                                .map(obj -> new HourlyAnalyticsResponse(
                                                ((Number) obj[0]).intValue(),
                                                ((Number) obj[1]).longValue()))
                                .toList();
        }

        public List<HistoryAnalyticsResponse> getHistoryAnalytics(int days) {

                LocalDateTime startDate = LocalDateTime.now().minusDays(days);

                return tokenRepository.getHistoryAnalytics(startDate)
                                .stream()
                                .map(obj -> new HistoryAnalyticsResponse(
                                                ((Date) obj[0]).toLocalDate(),
                                                ((Number) obj[1]).longValue(),
                                                ((Number) obj[2]).longValue()))
                                .toList();
        }
}
