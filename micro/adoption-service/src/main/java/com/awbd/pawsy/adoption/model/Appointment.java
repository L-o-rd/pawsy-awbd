package com.awbd.pawsy.adoption.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_date")
    private LocalDate appointmentDate;

    @Column(name = "scheduled_at_date")
    private LocalDateTime scheduledAtDate;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String adopter;

    @Column(name = "pet_id", nullable = false)
    private Long petId;

    @Column(name = "shelter_id")
    private Long shelterId;
}
