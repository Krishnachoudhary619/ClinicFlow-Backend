package com.clinicflow.backend.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final String SECRET = "your-very-secure-secret-key-minimum-256-bits-long-make-sure-to-change-this";
    private final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 15; // 15 mins

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    public String generateAccessToken(Long userId, Long clinicId, String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("clinic_id", clinicId)
                .claim("role", role)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
