package com.awbd.pawsy.pet.dto;

public record PetSummary(
    Long id,
    String name,
    String species,
    Integer age,
    String description,
    String status,
    String sex,
    Long shelterId,
    String photo
) {}
