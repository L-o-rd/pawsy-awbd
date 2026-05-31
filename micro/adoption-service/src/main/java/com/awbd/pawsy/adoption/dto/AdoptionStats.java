package com.awbd.pawsy.adoption.dto;

public record AdoptionStats(
    Long totalAdoptions,
    Long pendingAdoptions,
    Long totalAppointments,
    Long ongoingAppointments
) {}
