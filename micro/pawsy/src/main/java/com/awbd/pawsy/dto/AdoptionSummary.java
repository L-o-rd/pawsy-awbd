package com.awbd.pawsy.dto;

import java.time.LocalDateTime;

public record AdoptionSummary(
    Long id,
    Long petId,
    String adopterName,
    LocalDateTime requestDate,
    String status
) {}
