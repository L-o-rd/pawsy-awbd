package com.awbd.pawsy.dto;

public record PetStats(
    Long totalPets,
    Long availablePets,
    Long adoptedPets,
    Long totalShelters
) {}
