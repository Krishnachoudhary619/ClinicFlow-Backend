package com.clinicflow.backend.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token, Long> {

    List<Token> findByClinicDayIdAndStatusOrderByTokenNumberAsc(
            Long clinicDayId,
            Token.Status status);

    List<Token> findByClinicDayIdOrderByTokenNumberAsc(Long clinicDayId);
}
