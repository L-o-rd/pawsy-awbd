package com.awbd.pawsy.pet.dto;

import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.model.PetSex;

public record PetSummary(
    Long id,
    String name,
    String species,
    Integer age,
    String description,
    PetStatus status,
    PetSex sex,
    Long shelterId,
    String photo
) {}
