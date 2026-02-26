package com.clinicflow.backend.queue;

import lombok.Data;

@Data
public class CreateTokenRequest {
    private String patientName;
    private String patientPhone;
}
