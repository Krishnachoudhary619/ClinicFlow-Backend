package com.clinicflow.backend.auth;

import com.clinicflow.backend.user.User;
import com.clinicflow.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String tokenHash;

    private LocalDateTime expiresAt;

    private Boolean revoked = false;

    private String ipAddress;
    private String userAgent;
}
