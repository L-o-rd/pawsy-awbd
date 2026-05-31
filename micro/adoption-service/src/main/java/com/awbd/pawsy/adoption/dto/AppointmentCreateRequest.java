package com.awbd.pawsy.adoption.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record AppointmentCreateRequest (
    @NotNull(message = "Appointment date is required.")
    @FutureOrPresent(message = "Appointment Date cannot be in the past.")
    LocalDate appointmentDate
) {}
