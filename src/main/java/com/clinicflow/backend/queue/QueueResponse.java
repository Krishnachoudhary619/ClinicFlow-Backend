package com.clinicflow.backend.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class QueueResponse {

    private Token currentServing;
    private int waitingCount;
    private List<Token> waitingTokens;
}
