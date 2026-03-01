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
        private final ClinicDayService clinicDayService;

        @Transactional
        public Token createToken(CreateTokenRequest request) {

                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                Long clinicId = principal.getClinicId();

                Clinic clinic = clinicRepository.findById(clinicId)
                                .orElseThrow(() -> new ApiException("Clinic not found"));

                ClinicDay clinicDay = clinicDayService.getActiveClinicDay(clinicId);

                Integer lastTokenNumber = tokenRepository.findLastTokenNumber(clinicDay.getId(),
                                clinicDay.getCurrentCycle());

                int newTokenNumber = lastTokenNumber + 1;

                Token token = Token.builder()
                                .clinic(clinic)
                                .clinicDay(clinicDay)
                                .cycleNumber(clinicDay.getCurrentCycle())
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

                ClinicDay clinicDay = clinicDayService.getActiveClinicDay(clinicId);

                Token current = tokenRepository.findCurrentCalled(clinicDay.getId(), clinicDay.getCurrentCycle())
                                .orElse(null);

                List<Token> waiting = tokenRepository.findWaitingTokens(clinicDay.getId(), clinicDay.getCurrentCycle());

                return QueueResponse.builder()
                                .currentServing(current)
                                .waitingCount(waiting.size())
                                .waitingTokens(waiting)
                                .build();
        }

        @Transactional
        public QueueServeResult markCurrentAsServed() {

                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                Long clinicId = principal.getClinicId();

                ClinicDay clinicDay = clinicDayService.getActiveClinicDay(clinicId);

                StringBuilder message = new StringBuilder();

                // 1️⃣ Get current CALLED token
                Optional<Token> currentCalledOpt = tokenRepository.findCurrentCalled(clinicDay.getId(),
                                clinicDay.getCurrentCycle());

                Token servedToken = null;
                if (currentCalledOpt.isPresent()) {
                        servedToken = currentCalledOpt.get();
                        servedToken.setStatus(Token.Status.SERVED);
                        servedToken.setServedAt(LocalDateTime.now());
                        tokenRepository.save(servedToken);
                        message.append("Token #").append(servedToken.getTokenNumber()).append(" served. ");
                }

                // 2️⃣ Get next token (Try WAITING first)
                List<Token> waiting = tokenRepository.findWaitingTokens(clinicDay.getId(), clinicDay.getCurrentCycle());
                Token next = null;

                if (!waiting.isEmpty()) {
                        next = waiting.get(0);
                        next.setStatus(Token.Status.CALLED);
                        tokenRepository.save(next);
                        message.append("Called next token #").append(next.getTokenNumber()).append(".");
                } else {
                        if (servedToken != null) {
                                message.append("Queue is now empty.");
                        } else {
                                throw new ApiException("No tokens are currently being served or waiting.", "QUEUE_003");
                        }
                }

                // 3️⃣ Return updated queue and descriptive message
                return QueueServeResult.builder()
                                .queue(getCurrentQueue())
                                .message(message.toString().trim())
                                .build();
        }

        public PublicTokenStatusResponse getPublicTokenStatus(Long tokenId) {

                Token token = tokenRepository.findById(tokenId)
                                .orElseThrow(() -> new ApiException("Token not found", "TOKEN_001"));

                ClinicDay clinicDay = token.getClinicDay();
                Integer currentCycle = clinicDay.getCurrentCycle();

                // Only show active cycle tokens
                if (!token.getCycleNumber().equals(currentCycle)) {
                        throw new ApiException("This token belongs to a previous session", "TOKEN_002");
                }

                Integer currentServing = tokenRepository
                                .findCurrentCalled(clinicDay.getId(), currentCycle)
                                .map(Token::getTokenNumber)
                                .orElse(null);

                Long patientsAhead = tokenRepository.countPatientsAhead(clinicDay.getId(), currentCycle,
                                token.getTokenNumber());

                // Simple static estimate: 5 minutes per patient
                int estimatedWait = patientsAhead.intValue() * 5;

                return PublicTokenStatusResponse.builder()
                                .tokenNumber(token.getTokenNumber())
                                .status(token.getStatus().name())
                                .currentServing(currentServing)
                                .patientsAhead(patientsAhead.intValue())
                                .estimatedWaitMinutes(estimatedWait)
                                .build();
        }

        @Transactional
        public QueueResponse skipCurrentToken() {

                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                Long clinicId = principal.getClinicId();
                ClinicDay clinicDay = clinicDayService.getActiveClinicDay(clinicId);

                // 1️⃣ Find current CALLED token
                Optional<Token> currentCalledOpt = tokenRepository.findCurrentCalled(clinicDay.getId(),
                                clinicDay.getCurrentCycle());

                if (currentCalledOpt.isEmpty()) {
                        throw new ApiException("No token is currently being served", "QUEUE_002");
                }

                Token current = currentCalledOpt.get();

                // 2️⃣ Move it to DELAYED
                current.setStatus(Token.Status.DELAYED);
                tokenRepository.save(current);

                // 3️⃣ Find next WAITING token
                List<Token> waiting = tokenRepository.findWaitingTokens(clinicDay.getId(), clinicDay.getCurrentCycle());

                if (!waiting.isEmpty()) {
                        Token next = waiting.get(0);
                        next.setStatus(Token.Status.CALLED);
                        tokenRepository.save(next);
                }

                return getCurrentQueue();
        }

        @Transactional
        public String resetTokens() {

                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                Long clinicId = principal.getClinicId();
                ClinicDay clinicDay = clinicDayRepository
                                .findActiveClinicDayForUpdate(clinicId)
                                .orElseThrow(() -> new ApiException("No active clinic day", "QUEUE_001"));

                clinicDay.setCurrentCycle(clinicDay.getCurrentCycle() + 1);
                clinicDayRepository.save(clinicDay);

                return "Token reset successfully. New cycle started.";
        }

        @Transactional
        public String startNewDay() {

                UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal();

                Long clinicId = principal.getClinicId();
                LocalDate today = LocalDate.now();

                clinicDayRepository
                                .findActiveClinicDayForUpdate(clinicId)
                                .ifPresent(day -> {
                                        day.setIsClosed(true);
                                        day.setClosedAt(LocalDateTime.now());
                                        clinicDayRepository.save(day);
                                });

                Clinic clinic = clinicRepository.findById(clinicId)
                                .orElseThrow(() -> new ApiException("Clinic not found", "CLINIC_001"));

                ClinicDay newDay = ClinicDay.builder()
                                .clinic(clinic)
                                .date(today)
                                .isClosed(false)
                                .currentCycle(1)
                                .build();

                clinicDayRepository.save(newDay);

                return "New day started successfully.";
        }
}
