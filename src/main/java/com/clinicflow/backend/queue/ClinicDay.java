package com.clinicflow.backend.queue;

import com.clinicflow.backend.clinic.Clinic;
import com.clinicflow.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "clinic_days", uniqueConstraints = @UniqueConstraint(columnNames = { "clinic_id", "date" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicDay extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "clinic_id", nullable = false)
    private Clinic clinic;

    private LocalDate date;

    @Builder.Default
    private Boolean isClosed = false;

    @Builder.Default
    @Column(nullable = false)
    private Integer currentCycle = 1;
}
