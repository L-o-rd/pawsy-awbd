package com.awbd.pawsy.adoption.controller;

import com.awbd.pawsy.adoption.service.AppointmentService;
import com.awbd.pawsy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
            var user = userService.getByUsername(username);
            var appointment = appointmentService.get(id);

            if (!appointment.getAdopter().getId().equals(user.getId())) {
                throw new AccessDeniedException("Appointment is not yours.");
            }

            appointmentService.cancel(id);
            redirect.addFlashAttribute("successMessage", "Appointment cancelled.");
            return "redirect:/appointments/my";
        } catch (IllegalStateException ise) {
            redirect.addFlashAttribute("errorMessage", ise.getMessage());
            return "redirect:/appointments/my";
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Failed to cancel appointment.");
            return "redirect:/appointments/my";
        }
    }
}
