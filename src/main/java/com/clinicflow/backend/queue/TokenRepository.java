package com.clinicflow.backend.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByClinicDayIdAndStatusOrderByTokenNumberAsc(
            Long clinicDayId,
            Token.Status status);

    List<Token> findByClinicDayIdOrderByTokenNumberAsc(Long clinicDayId);

    @Query("""
                SELECT COALESCE(MAX(t.tokenNumber), 0)
                FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.cycleNumber = :cycleNumber
            """)
    Integer findLastTokenNumber(Long clinicDayId, Integer cycleNumber);

    @Query("""
                SELECT t FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.cycleNumber = :cycleNumber
                AND t.status = com.clinicflow.backend.queue.Token$Status.WAITING
                ORDER BY t.tokenNumber ASC
            """)
    List<Token> findWaitingTokens(Long clinicDayId, Integer cycleNumber);

    @Query("""
                SELECT t FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.cycleNumber = :cycleNumber
                AND t.status = com.clinicflow.backend.queue.Token$Status.CALLED
            """)
    Optional<Token> findCurrentCalled(Long clinicDayId, Integer cycleNumber);

    @Query("""
                SELECT COUNT(t)
                FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.cycleNumber = :cycleNumber
                AND t.status = com.clinicflow.backend.queue.Token$Status.WAITING
                AND t.tokenNumber < :tokenNumber
            """)
    Long countPatientsAhead(Long clinicDayId, Integer cycleNumber, Integer tokenNumber);

    @Query("""
                SELECT t FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.cycleNumber = :cycleNumber
                AND t.tokenNumber = :tokenNumber
            """)
    Optional<Token> findTokenByNumberInClinicDay(Long clinicDayId, Integer cycleNumber, Integer tokenNumber);

    @Query("""
                SELECT t FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.cycleNumber = :cycleNumber
                AND t.status = com.clinicflow.backend.queue.Token$Status.DELAYED
                ORDER BY t.tokenNumber ASC
            """)
    List<Token> findDelayedTokensOrdered(Long clinicDayId, Integer cycleNumber);

    @Query("""
                SELECT
                    COUNT(t),
                    COALESCE(SUM(CASE WHEN t.status = 'SERVED' THEN 1 ELSE 0 END), 0),
                    COALESCE(SUM(CASE WHEN t.status = 'DELAYED' THEN 1 ELSE 0 END), 0),
                    COALESCE(AVG(
                        CASE
                            WHEN t.servedAt IS NOT NULL
                            THEN CAST(timestampdiff(MINUTE, t.createdAt, t.servedAt) AS double)
                        END
                    ), 0.0)
                FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
            """)
    List<Object[]> getClinicDayAnalytics(@Param("clinicDayId") Long clinicDayId);

    @Query("""
                SELECT
                    hour(t.createdAt),
                    COUNT(t)
                FROM Token t
                WHERE CAST(t.createdAt AS date) = :date
                GROUP BY hour(t.createdAt)
                ORDER BY hour(t.createdAt)
            """)
    List<Object[]> getHourlyAnalytics(@Param("date") LocalDate date);

    @Query("""
                SELECT
                    CAST(t.createdAt AS date),
                    COUNT(t),
                    COALESCE(SUM(CASE WHEN t.status = 'SERVED' THEN 1 ELSE 0 END), 0)
                FROM Token t
                WHERE t.createdAt >= :startDate
                GROUP BY CAST(t.createdAt AS date)
                ORDER BY CAST(t.createdAt AS date)
            """)
    List<Object[]> getHistoryAnalytics(@Param("startDate") LocalDateTime startDate);
}
