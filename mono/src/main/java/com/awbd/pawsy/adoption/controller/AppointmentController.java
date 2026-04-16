package com.awbd.pawsy.adoption.controller;

import com.awbd.pawsy.adoption.service.AppointmentService;
import com.awbd.pawsy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static java.util.Objects.requireNonNull;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;
    private final UserService userService;

    @GetMapping("/my")
    public String myAppointments(Model model) {
        var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var user = userService.getByUsername(username);
        var appointments = appointmentService.getForAdopter(user.getId());
        model.addAttribute("appointments", appointments);
        return "appointments/my";
    }
}
