package com.awbd.pawsy.pet.dto;

public record ShelterSummary(
    Long id,
    String name,
    String location,
    String email,
    String phone,
    String manager
) {}
