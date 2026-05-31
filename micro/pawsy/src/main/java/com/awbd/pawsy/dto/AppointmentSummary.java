package com.awbd.pawsy.dto;

import java.time.LocalDateTime;
import java.time.LocalDate;

public record AppointmentSummary(
    Long id,
    Long petId,
    String adopterName,
    LocalDate appointmentDate,
    LocalDateTime scheduledAtDate,
    String status
) {}
