package com.awbd.pawsy.adoption.model;

import com.awbd.pawsy.pet.model.Pet;
import com.awbd.pawsy.user.model.User;
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
        uniqueConstraints = @UniqueConstraint(columnNames = {"adopter_id", "pet_id"}))
public class Adoption {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "adopter_id", nullable = false)
    private User adopter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    @Column(name = "approval_date")
    private LocalDateTime approvalDate;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Enumerated(EnumType.STRING)
    private AdoptionStatus status;
}
