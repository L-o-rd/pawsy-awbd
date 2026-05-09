package com.awbd.pawsy.pet.model;

import java.time.LocalDateTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"adopter", "shelter_id"}),
})
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer rating;
    private String comment;

    @Column(name ="created_at")
    private LocalDateTime createdAt;

    @Column(name ="edited_at")
    private LocalDateTime editedAt;
    private String adopter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shelter_id", nullable = false)
    private Shelter shelter;
}
