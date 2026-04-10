package com.awbd.pawsy.pet.dto;

public record PetCreateRequest(
    String name,
    String species,
    Integer age,
    String description,
    String sex,
    String photo
) {}
