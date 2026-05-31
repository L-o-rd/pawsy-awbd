package com.awbd.pawsy.adoption.dto;

import java.time.LocalDateTime;

public record AdoptionSummary(
    Long id,
    Long petId,
    String adopterName,
    String status,
    LocalDateTime requestDate
) {}
