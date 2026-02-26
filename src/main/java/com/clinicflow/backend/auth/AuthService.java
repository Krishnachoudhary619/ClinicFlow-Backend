package com.clinicflow.backend.auth;

import com.clinicflow.backend.common.ApiException;
import com.clinicflow.backend.user.User;
import com.clinicflow.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final UserRepository userRepository;
        private final RefreshTokenRepository refreshTokenRepository;
        private final JwtService jwtService;
        private final PasswordEncoder passwordEncoder;

        public AuthResponse login(String email, String password) {

                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new ApiException("User not registered or wrong credentials",
                                                "AUTH_INVALID_CREDENTIALS"));

                if (!passwordEncoder.matches(password, user.getPasswordHash())) {
                        throw new ApiException("User not registered or wrong credentials", "AUTH_INVALID_CREDENTIALS");
                }

                String accessToken = jwtService.generateAccessToken(
                                user.getId(),
                                user.getClinic().getId(),
                                user.getRole().name());

                String refreshToken = UUID.randomUUID().toString();
                String refreshTokenHash = passwordEncoder.encode(refreshToken);

                RefreshToken tokenEntity = RefreshToken.builder()
                                .user(user)
                                .tokenHash(refreshTokenHash)
                                .expiresAt(LocalDateTime.now().plusDays(7))
                                .revoked(false)
                                .build();

                refreshTokenRepository.save(tokenEntity);

                return new AuthResponse(accessToken, refreshToken);
        }

        public AuthResponse refresh(String refreshTokenRaw) {

                RefreshToken token = refreshTokenRepository.findAll()
                                .stream()
                                .filter(rt -> passwordEncoder.matches(refreshTokenRaw, rt.getTokenHash()))
                                .findFirst()
                                .orElseThrow(() -> new ApiException("Invalid refresh token", "AUTH_TOKEN_INVALID"));

                if (token.getExpiresAt().isBefore(LocalDateTime.now()) || token.getRevoked()) {
                        throw new ApiException("Refresh token expired", "AUTH_TOKEN_EXPIRED");
                }

                token.setRevoked(true);
                refreshTokenRepository.save(token);

                User user = token.getUser();

                String newAccessToken = jwtService.generateAccessToken(
                                user.getId(),
                                user.getClinic().getId(),
                                user.getRole().name());

                String newRefreshToken = UUID.randomUUID().toString();
                String newRefreshHash = passwordEncoder.encode(newRefreshToken);

                RefreshToken newTokenEntity = RefreshToken.builder()
                                .user(user)
                                .tokenHash(newRefreshHash)
                                .expiresAt(LocalDateTime.now().plusDays(7))
                                .revoked(false)
                                .build();

                refreshTokenRepository.save(newTokenEntity);

                return new AuthResponse(newAccessToken, newRefreshToken);
        }
}
