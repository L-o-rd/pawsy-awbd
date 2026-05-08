package com.awbd.pawsy.dto;

public record PetResponse(
    Long id,
    String name,
    String species,
    Integer age,
    String description,
    String photo,
    String status,
    String sex,
    Long shelterId
) {}
