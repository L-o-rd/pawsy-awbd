package com.awbd.pawsy.dto;

import java.time.LocalDateTime;

public record ReviewResponse(
    Long id,
    Integer rating,
    String comment,
    LocalDateTime createdAt,
    LocalDateTime editedAt,
    String username,
    Long shelterId
) {}
