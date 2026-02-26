package com.clinicflow.backend.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByClinicDayIdAndStatusOrderByTokenNumberAsc(
            Long clinicDayId,
            Token.Status status);

    List<Token> findByClinicDayIdOrderByTokenNumberAsc(Long clinicDayId);

    @org.springframework.data.jpa.repository.Query("""
                SELECT COALESCE(MAX(t.tokenNumber), 0)
                FROM Token t
                WHERE t.clinicDay.id = :clinicDayId
            """)
    Integer findLastTokenNumber(Long clinicDayId);
}
