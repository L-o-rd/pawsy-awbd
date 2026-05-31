package com.awbd.pawsy.controller;

import com.awbd.pawsy.client.AdoptionClient;
import com.awbd.pawsy.security.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/appointments")
public class AppointmentController {
    private final AdoptionClient adoptionClient;

    @GetMapping("/my")
    public String myAppointments(Model model) {
        var username = ContextUtils.getCurrentUsername();
        var appointments = adoptionClient.getAppointmentsForAdopter(username);
        model.addAttribute("appointments", appointments);
        return "appointments/my";
    }

    @PostMapping("/{id}/cancel")
    public String cancel(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            var username = ContextUtils.getCurrentUsername();
            var appointment = adoptionClient.getAppointmentById(id).orElseThrow();

            if (!appointment.adopterName().equals(username))
                throw new AccessDeniedException("Appointment is not yours.");

            adoptionClient.cancelAppointment(id);
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
