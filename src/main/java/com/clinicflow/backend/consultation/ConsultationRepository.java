package com.clinicflow.backend.consultation;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    Optional<Consultation> findByTokenId(Long tokenId);

    List<Consultation> findByDoctorId(Long doctorId);
}
