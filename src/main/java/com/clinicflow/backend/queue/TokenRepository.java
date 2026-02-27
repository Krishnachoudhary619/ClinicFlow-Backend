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
                WHERE t.clinic.id = :clinicId
                AND t.clinicDay.date = CURRENT_DATE
                AND t.cycleNumber = :cycleNumber
                AND t.tokenNumber = :tokenNumber
            """)
    Optional<Token> findTodayToken(Long clinicId, Integer cycleNumber, Integer tokenNumber);

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
                    SUM(CASE WHEN t.status = 'SERVED' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN t.status = 'DELAYED' THEN 1 ELSE 0 END),
                    AVG(
                        CASE
                            WHEN t.servedAt IS NOT NULL
                            THEN CAST(FUNCTION('TIMESTAMPDIFF', MINUTE, t.createdAt, t.servedAt) AS double)
                        END
                    )
                FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
            """)
    Object[] getTodayAnalytics(@Param("clinicDayId") Long clinicDayId);

    @Query("""
                SELECT
                    FUNCTION('HOUR', t.createdAt),
                    COUNT(t)
                FROM Token t
                WHERE DATE(t.createdAt) = :date
                GROUP BY FUNCTION('HOUR', t.createdAt)
                ORDER BY FUNCTION('HOUR', t.createdAt)
            """)
    List<Object[]> getHourlyAnalytics(@Param("date") LocalDate date);

    @Query("""
                SELECT
                    DATE(t.createdAt),
                    COUNT(t),
                    SUM(CASE WHEN t.status = 'SERVED' THEN 1 ELSE 0 END)
                FROM Token t
                WHERE t.createdAt >= :startDate
                GROUP BY DATE(t.createdAt)
                ORDER BY DATE(t.createdAt)
            """)
    List<Object[]> getHistoryAnalytics(@Param("startDate") LocalDateTime startDate);
}
