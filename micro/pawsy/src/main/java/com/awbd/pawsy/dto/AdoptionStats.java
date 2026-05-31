package com.awbd.pawsy.dto;

public record AdoptionStats(
    Long totalAdoptions,
    Long pendingAdoptions,
    Long totalAppointments,
    Long ongoingAppointments
) {}
