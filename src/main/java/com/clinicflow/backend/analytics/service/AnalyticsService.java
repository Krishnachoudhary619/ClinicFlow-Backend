package com.clinicflow.backend.analytics.service;

import com.clinicflow.backend.analytics.dto.HistoryAnalyticsResponse;
import com.clinicflow.backend.analytics.dto.HourlyAnalyticsResponse;
import com.clinicflow.backend.analytics.dto.TodayAnalyticsResponse;
import com.clinicflow.backend.queue.ClinicDay;
import com.clinicflow.backend.queue.ClinicDayService;
import com.clinicflow.backend.queue.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

        private final TokenRepository tokenRepository;
        private final ClinicDayService clinicDayService;

        public TodayAnalyticsResponse getTodayAnalytics(Long clinicId) {

                ClinicDay today = clinicDayService.getActiveClinicDay(clinicId);

                List<Object[]> queryResult = tokenRepository.getTodayAnalytics(today.getId());

                if (queryResult == null || queryResult.isEmpty()) {
                        log.info("No analytics data found for clinicId: {}", clinicId);
                        return new TodayAnalyticsResponse(0L, 0L, 0L, 0.0);
                }

                Object[] result = queryResult.get(0);

                // Debug log to see exactly what we're dealing with
                if (result != null) {
                        log.info("Query Result[0] type: {}, Length: {}", result.getClass().getName(), result.length);
                }

                // Handle Hibernate 6 potentially wrapping the result row in another array
                if (result != null && result.length == 1 && result[0] instanceof Object[]) {
                        log.info("Unwrapping nested Object[] in result");
                        result = (Object[]) result[0];
                }

                if (result == null || result.length < 4) {
                        log.warn("Analytics query returned incomplete result. Result: {}",
                                        (result == null ? "null" : "length " + result.length));
                        return new TodayAnalyticsResponse(0L, 0L, 0L, 0.0);
                }

                Long totalGenerated = resultSafeLong(result[0]);
                Long totalServed = resultSafeLong(result[1]);
                Long totalSkipped = resultSafeLong(result[2]);
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
                                .map(obj -> {
                                        LocalDate date;
                                        if (obj[0] instanceof LocalDate ld) {
                                                date = ld;
                                        } else if (obj[0] instanceof java.sql.Date sd) {
                                                date = sd.toLocalDate();
                                        } else {
                                                log.warn("Unexpected date type in history analytics: {}",
                                                                obj[0].getClass());
                                                date = LocalDate.now(); // Fallback
                                        }

                                        return new HistoryAnalyticsResponse(
                                                        date,
                                                        ((Number) obj[1]).longValue(),
                                                        resultSafeLong(obj[2]));
                                })
                                .toList();
        }

        private Long resultSafeLong(Object obj) {
                return obj != null ? ((Number) obj).longValue() : 0L;
        }
}
