package com.clinicflow.backend.queue;

import com.clinicflow.backend.clinic.Clinic;
import com.clinicflow.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    @ManyToOne
    @JoinColumn(name = "clinic_day_id", nullable = false)
    private ClinicDay clinicDay;

    private Integer tokenNumber;

    private String patientName;
    private String patientPhone;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Integer queuePosition;

    private LocalDateTime graceExpiresAt;

    private LocalDateTime servedAt;

    public enum Status {
        WAITING,
        CALLED,
        SERVED,
        SKIPPED,
        DELAYED,
        CANCELLED
    }
}
