package com.awbd.pawsy.adoption.controller;

import com.awbd.pawsy.adoption.dto.AppointmentCreateRequest;
import com.awbd.pawsy.adoption.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @GetMapping("/by-user/{username}")
    public ResponseEntity<?> getForUser(@PathVariable String username) {
        var apps = appointmentService.getForAdopter(username);
        return ResponseEntity.ok().body(apps);
    }

    @GetMapping("/by-pet/{petId}/booked")
    public ResponseEntity<?> getBookedDatesForPet(@PathVariable Long petId) {
        var dates = appointmentService.getBookedDates(petId).stream().map(LocalDate::toString).toList();
        return ResponseEntity.ok().body(dates);
    }

    @GetMapping("/{appId}")
    public ResponseEntity<?> get(@PathVariable Long appId) {
        return ResponseEntity.ok().body(appointmentService.getById(appId));
    }

    @PostMapping("/{appId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long appId) {
        appointmentService.cancel(appId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/for-pet/{petId}/at/{shelterId}/for-user/{username}")
    public ResponseEntity<?> create(@PathVariable Long petId,
                                    @PathVariable Long shelterId,
                                    @PathVariable String username,
                                    @RequestBody AppointmentCreateRequest dto) {
        appointmentService.create(username, petId, shelterId, dto);
        return ResponseEntity.ok().build();
    }
}
