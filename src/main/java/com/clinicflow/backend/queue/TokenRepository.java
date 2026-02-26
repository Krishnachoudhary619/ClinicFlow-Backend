package com.clinicflow.backend.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByClinicDayIdAndStatusOrderByTokenNumberAsc(
            Long clinicDayId,
            Token.Status status);

    List<Token> findByClinicDayIdOrderByTokenNumberAsc(Long clinicDayId);

    @Query("""
                SELECT COALESCE(MAX(t.tokenNumber), 0)
                FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
            """)
    Integer findLastTokenNumber(Long clinicDayId);

    @Query("""
                SELECT t FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.status = com.clinicflow.backend.queue.Token$Status.WAITING
                ORDER BY t.tokenNumber ASC
            """)
    List<Token> findNextWaiting(Long clinicDayId);

    @Query("""
                SELECT t FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.status = com.clinicflow.backend.queue.Token$Status.CALLED
            """)
    Optional<Token> findCurrentCalled(Long clinicDayId);

    @Query("""
                SELECT COUNT(t)
                FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.status = com.clinicflow.backend.queue.Token$Status.WAITING
                AND t.tokenNumber < :tokenNumber
            """)
    Long countPatientsAhead(Long clinicDayId, Integer tokenNumber);

    @Query("""
                SELECT t FROM Token t
                WHERE t.clinic.id = :clinicId
                AND t.clinicDay.date = CURRENT_DATE
                AND t.tokenNumber = :tokenNumber
            """)
    Optional<Token> findTodayToken(Long clinicId, Integer tokenNumber);

    @Query("""
                SELECT t FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.status = com.clinicflow.backend.queue.Token$Status.WAITING
                ORDER BY t.tokenNumber ASC
            """)
    List<Token> findWaitingTokensOrdered(Long clinicDayId);

    @Query("""
                SELECT t FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
                AND t.status = com.clinicflow.backend.queue.Token$Status.DELAYED
                ORDER BY t.tokenNumber ASC
            """)
    List<Token> findDelayedTokensOrdered(Long clinicDayId);
}
