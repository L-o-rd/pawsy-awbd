package com.awbd.pawsy.adoption.model;

import com.awbd.pawsy.pet.model.Pet;
import com.awbd.pawsy.user.model.User;
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

    @ManyToOne(optional = false)
    @JoinColumn(name = "adopter_id", nullable = false)
    private User adopter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
}
