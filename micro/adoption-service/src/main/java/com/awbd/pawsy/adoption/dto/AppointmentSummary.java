package com.awbd.pawsy.adoption.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppointmentSummary(
    Long id,
    Long petId,
    String adopterName,
    LocalDate appointmentDate,
    LocalDateTime scheduledAtDate,
    String status
) {}
