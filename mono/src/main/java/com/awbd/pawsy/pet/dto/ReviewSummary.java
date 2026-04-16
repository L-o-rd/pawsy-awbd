package com.awbd.pawsy.pet.dto;

import java.time.LocalDateTime;

public record ReviewSummary(
    LocalDateTime createdAt,
    LocalDateTime editedAt,
    String username,
    Integer rating,
    String comment
) {}
