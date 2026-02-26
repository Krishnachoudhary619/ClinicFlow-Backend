package com.clinicflow.backend.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface ClinicDayRepository extends JpaRepository<ClinicDay, Long> {
    Optional<ClinicDay> findByClinicIdAndDate(Long clinicId, LocalDate date);
}
