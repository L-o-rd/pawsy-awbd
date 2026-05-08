package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shelters")
public class ShelterController {
    private final ShelterService shelterService;

    @GetMapping
    public ResponseEntity<?> all() {
        return ResponseEntity.ok().body(shelterService.all());
    }
}
