package com.clinicflow.backend.queue;

import com.clinicflow.backend.common.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClinicDayService {

    private final ClinicDayRepository clinicDayRepository;

    public ClinicDay getActiveClinicDay(Long clinicId) {
        return clinicDayRepository
                .findTopByClinicIdAndIsClosedFalseOrderByCreatedAtDesc(clinicId)
                .orElseThrow(() -> new ApiException("No active clinic day. Please start a new day.", "QUEUE_001"));
    }
}
