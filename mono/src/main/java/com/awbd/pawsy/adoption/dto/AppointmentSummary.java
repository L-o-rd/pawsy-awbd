package com.awbd.pawsy.adoption.dto;

import com.awbd.pawsy.pet.dto.PetSummary;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record AppointmentSummary(
    Long id,
    PetSummary pet,
    LocalDate appointmentDate,
    LocalDateTime scheduledAtDate,
    String status
) {}
