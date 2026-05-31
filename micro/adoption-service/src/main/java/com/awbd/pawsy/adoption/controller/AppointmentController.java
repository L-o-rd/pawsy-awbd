package com.awbd.pawsy.adoption.controller;

import com.awbd.pawsy.adoption.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{appId}")
    public ResponseEntity<?> get(@PathVariable Long appId) {
        return ResponseEntity.ok().body(appointmentService.getById(appId));
    }

    @PostMapping("/{appId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long appId) {
        appointmentService.cancel(appId);
        return ResponseEntity.ok().build();
    }
}
