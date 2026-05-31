package com.awbd.pawsy.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;

public record AppointmentResponse(
    Long id,
    PetResponse pet,
    String adopterName,
    LocalDate appointmentDate,
    LocalDateTime scheduledAtDate,
    String status
) {}
