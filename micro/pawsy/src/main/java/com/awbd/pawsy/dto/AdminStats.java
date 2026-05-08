package com.awbd.pawsy.dto;

public record AdminStats(
    Long totalPets,
    Long availablePets,
    Long adoptedPets,

    Long totalUsers,
    Long totalShelters,

    Long totalAdoptions,
    Long pendingAdoptions,

    Long totalAppointments,
    Long ongoingAppointments
) {}
