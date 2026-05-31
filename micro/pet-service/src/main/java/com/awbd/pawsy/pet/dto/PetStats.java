package com.awbd.pawsy.pet.dto;

public record PetStats(
    Long totalPets,
    Long availablePets,
    Long adoptedPets,
    Long totalShelters
) {}
