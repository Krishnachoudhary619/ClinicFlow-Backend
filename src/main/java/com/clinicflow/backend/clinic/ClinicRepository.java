package com.clinicflow.backend.clinic;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClinicRepository extends JpaRepository<Clinic, Long> {
    List<Clinic> findByIsActiveTrue();

    List<Clinic> findByCityIgnoreCase(String city);
}
