package com.awbd.pawsy.adoption.controller;

import com.awbd.pawsy.adoption.dto.AdoptionStats;
import com.awbd.pawsy.adoption.model.AdoptionStatus;
import com.awbd.pawsy.adoption.model.AppointmentStatus;
import com.awbd.pawsy.adoption.service.AdoptionService;
import com.awbd.pawsy.adoption.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsController {
    private final AppointmentService appointmentService;
    private final AdoptionService adoptionService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        var stats = new AdoptionStats(adoptionService.count(),
                adoptionService.countByStatus(AdoptionStatus.Pending),
                appointmentService.count(),
                appointmentService.countByStatus(AppointmentStatus.Ongoing));
        return ResponseEntity.ok().body(stats);
    }

    @GetMapping("/is-pet-free/{petId}")
    public ResponseEntity<?> isPetFree(@PathVariable Long petId) {
        var appFree = !appointmentService.anyForPet(petId);
        var adoptFree = !adoptionService.anyForPet(petId);
        return ResponseEntity.ok().body(appFree && adoptFree);
    }
}
