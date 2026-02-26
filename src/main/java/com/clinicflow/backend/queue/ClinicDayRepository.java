package com.clinicflow.backend.queue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;
import java.time.LocalDate;
import java.util.Optional;

public interface ClinicDayRepository extends JpaRepository<ClinicDay, Long> {
    Optional<ClinicDay> findByClinicIdAndDate(Long clinicId, LocalDate date);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT cd FROM ClinicDay cd WHERE cd.clinic.id = :clinicId AND cd.date = :date")
    Optional<ClinicDay> findByClinicIdAndDateForUpdate(Long clinicId, LocalDate date);
}
