package com.awbd.pawsy.pet.dto;

import java.time.LocalDateTime;

public record ReviewSummary(
    Long id,
    LocalDateTime createdAt,
    LocalDateTime editedAt,
    String username,
    Integer rating,
    String comment
) {}
