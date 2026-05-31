package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.dto.PetStats;
import com.awbd.pawsy.pet.model.PetStatus;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.pet.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stats")
public class StatsController {
    private final ShelterService shelterService;
    private final PetService petService;

    @GetMapping
    public ResponseEntity<?> getAll() {
        var stats = new PetStats(petService.count(),
                petService.countByStatus(PetStatus.Available),
                petService.countByStatus(PetStatus.Adopted),
                shelterService.count());
        return ResponseEntity.ok().body(stats);
    }
}
