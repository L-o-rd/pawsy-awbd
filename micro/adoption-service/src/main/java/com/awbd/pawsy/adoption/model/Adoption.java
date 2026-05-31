package com.awbd.pawsy.adoption.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "adoptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"adopter", "pet_id"}))
public class Adoption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String adopter;

    @Column(name = "pet_id")
    private Long petId;

    @Column(name = "shelter_id")
    private Long shelterId;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    private AdoptionStatus status;
}