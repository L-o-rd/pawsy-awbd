package com.awbd.pawsy.user.service;

import com.awbd.pawsy.adoption.model.AdoptionStatus;
import com.awbd.pawsy.adoption.model.AppointmentStatus;
import com.awbd.pawsy.adoption.service.AdoptionService;
import com.awbd.pawsy.adoption.service.AppointmentService;
import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.pet.service.ShelterService;
import com.awbd.pawsy.user.dto.AdminStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AppointmentService appointmentService;
    private final AdoptionService adoptionService;
    private final ShelterService shelterService;
    private final UserService userService;
    private final PetService petService;

    public AdminStats getStats() {
        return new AdminStats(petService.count(),
                petService.countByStatus(PetStatus.Available),
                petService.countByStatus(PetStatus.Adopted),
                userService.count(),
                shelterService.count(),
                adoptionService.count(),
                adoptionService.countByStatus(AdoptionStatus.Pending),
                appointmentService.count(),
                appointmentService.countByStatus(AppointmentStatus.Ongoing));
    }
}
