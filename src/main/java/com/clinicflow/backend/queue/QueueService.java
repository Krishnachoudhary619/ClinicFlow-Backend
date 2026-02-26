package com.clinicflow.backend.queue;

import com.clinicflow.backend.auth.UserPrincipal;
import com.clinicflow.backend.clinic.Clinic;
import com.clinicflow.backend.clinic.ClinicRepository;
import com.clinicflow.backend.common.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class QueueService {

        private final ClinicRepository clinicRepository;
        private final ClinicDayRepository clinicDayRepository;
        private final TokenRepository tokenRepository;

        @Transactional
        public Token createToken(CreateTokenRequest request) {

                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                Long clinicId = principal.getClinicId();

                Clinic clinic = clinicRepository.findById(clinicId)
                                .orElseThrow(() -> new ApiException("Clinic not found"));

                LocalDate today = LocalDate.now();

                ClinicDay clinicDay = clinicDayRepository
                                .findByClinicIdAndDate(clinicId, today)
                                .orElseGet(() -> {
                                        ClinicDay newDay = ClinicDay.builder()
                                                        .clinic(clinic)
                                                        .date(today)
                                                        .isClosed(false)
                                                        .build();
                                        return clinicDayRepository.save(newDay);
                                });

                Integer lastTokenNumber = tokenRepository.findLastTokenNumber(clinicDay.getId());

                int newTokenNumber = lastTokenNumber + 1;

                Token token = Token.builder()
                                .clinic(clinic)
                                .clinicDay(clinicDay)
                                .tokenNumber(newTokenNumber)
                                .patientName(request.getPatientName())
                                .patientPhone(request.getPatientPhone())
                                .status(Token.Status.WAITING)
                                .queuePosition(newTokenNumber)
                                .build();

                return tokenRepository.save(token);
        }

        public QueueResponse getCurrentQueue() {

                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                Long clinicId = principal.getClinicId();

                LocalDate today = LocalDate.now();

                ClinicDay clinicDay = clinicDayRepository
                                .findByClinicIdAndDate(clinicId, today)
                                .orElseThrow(() -> new ApiException("No active clinic day", "QUEUE_001"));

                Token current = tokenRepository.findCurrentCalled(clinicDay.getId())
                                .orElse(null);

                List<Token> waiting = tokenRepository.findNextWaiting(clinicDay.getId());

                return QueueResponse.builder()
                                .currentServing(current)
                                .waitingCount(waiting.size())
                                .waitingTokens(waiting)
                                .build();
        }

        @Transactional
        public QueueResponse markCurrentAsServed() {

                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                Long clinicId = principal.getClinicId();

                LocalDate today = LocalDate.now();

                ClinicDay clinicDay = clinicDayRepository
                                .findByClinicIdAndDate(clinicId, today)
                                .orElseThrow(() -> new ApiException("No active clinic day", "QUEUE_001"));

                // 1️⃣ Get current CALLED token
                Optional<Token> currentCalledOpt = tokenRepository.findCurrentCalled(clinicDay.getId());

                if (currentCalledOpt.isPresent()) {
                        Token current = currentCalledOpt.get();
                        current.setStatus(Token.Status.SERVED);
                        current.setServedAt(LocalDateTime.now());
                        tokenRepository.save(current);
                }

                // 2️⃣ Get next WAITING token
                List<Token> waiting = tokenRepository.findNextWaiting(clinicDay.getId());

                if (!waiting.isEmpty()) {
                        Token next = waiting.get(0);
                        next.setStatus(Token.Status.CALLED);
                        tokenRepository.save(next);
                }

                // 3️⃣ Return updated queue
                return getCurrentQueue();
        }
}
