package com.clinicflow.backend.queue;

import com.clinicflow.backend.common.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ClinicDayService {

    private final ClinicDayRepository clinicDayRepository;

    public ClinicDay getActiveClinicDay(Long clinicId) {
        return clinicDayRepository.findByClinicIdAndDate(clinicId, LocalDate.now())
                .orElseThrow(() -> new ApiException("No active clinic day found for today", "ANALYTICS_001"));
    }
}
