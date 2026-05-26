package com.awbd.pawsy.adoption.controller;

import com.awbd.pawsy.adoption.dto.AdoptionCreateRequest;
import com.awbd.pawsy.adoption.service.AdoptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/adoptions")
public class AdoptionController {
    private final AdoptionService adoptionService;

    @GetMapping("/by-shelter/{shelterId}")
    public ResponseEntity<?> getForShelter(@PathVariable Long shelterId) {
        var adoptions = adoptionService.getRequestsForShelter(shelterId);
        return ResponseEntity.ok().body(adoptions);
    }

    @GetMapping("/{adoptionId}")
    public ResponseEntity<?> get(@PathVariable Long adoptionId) {
        return ResponseEntity.ok().body(adoptionService.getById(adoptionId));
    }

    @PostMapping("/for-pet/{petId}/at/{shelterId}/for-user/{username}")
    public ResponseEntity<?> create(@PathVariable Long petId,
                                    @PathVariable Long shelterId,
                                    @PathVariable String username,
                                    @RequestBody AdoptionCreateRequest dto) {
        adoptionService.create(petId, shelterId, username, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{adoptionId}/approve")
    public ResponseEntity<?> approve(@PathVariable Long adoptionId) {
        adoptionService.approve(adoptionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{adoptionId}/reject")
    public ResponseEntity<?> reject(@PathVariable Long adoptionId) {
        adoptionService.reject(adoptionId);
        return ResponseEntity.ok().build();
    }
}
