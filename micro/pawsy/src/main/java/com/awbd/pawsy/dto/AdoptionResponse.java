package com.awbd.pawsy.dto;

import java.time.LocalDateTime;

public record AdoptionResponse(
    Long id,
    PetResponse pet,
    String adopterName,
    LocalDateTime approvalDate,
    LocalDateTime requestDate,
    String status
) {}
