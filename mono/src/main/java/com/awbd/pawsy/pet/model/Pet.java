package com.awbd.pawsy.pet.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pets")
public class Pet {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String species;
    private Integer age;
    private String description;
    private String photo;

    @Enumerated(EnumType.STRING)
    private PetStatus status;

    @Enumerated(EnumType.STRING)
    private PetSex sex;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shelter_id", nullable = false)
    private Shelter shelter;
}
