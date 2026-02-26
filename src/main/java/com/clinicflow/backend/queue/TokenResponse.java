package com.clinicflow.backend.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TokenResponse {

    private Long tokenId;
    private Integer tokenNumber;
    private Integer cycleNumber;
}
