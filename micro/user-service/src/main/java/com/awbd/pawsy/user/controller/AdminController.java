package com.awbd.pawsy.user.controller;

import com.awbd.pawsy.user.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<?> stats() {
        var stats = adminService.getStats();
        return ResponseEntity.ok().body(stats);
    }
}
