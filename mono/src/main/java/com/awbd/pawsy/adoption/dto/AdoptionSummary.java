package com.awbd.pawsy.adoption.dto;

import com.awbd.pawsy.pet.dto.PetSummary;

import java.time.LocalDateTime;

public record AdoptionSummary(
    Long id,
    PetSummary pet,
    String adopterName,
    String status,
    LocalDateTime requestDate
) {}
