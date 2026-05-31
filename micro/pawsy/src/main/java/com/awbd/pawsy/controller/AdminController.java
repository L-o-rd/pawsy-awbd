package com.awbd.pawsy.controller;

import com.awbd.pawsy.client.AdoptionClient;
import com.awbd.pawsy.client.PetClient;
import com.awbd.pawsy.client.UserClient;
import com.awbd.pawsy.dto.AdminStats;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {
    private final AdoptionClient adoptionClient;
    private final UserClient userClient;
    private final PetClient petClient;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        var adoptStats = adoptionClient.getStats();
        var userStats = userClient.getAdminStats();
        var petStats = petClient.getStats();
        var adminStats = new AdminStats(petStats.totalPets(),
                petStats.availablePets(),
                petStats.adoptedPets(),
                userStats.totalUsers(),
                petStats.totalShelters(),
                adoptStats.totalAdoptions(),
                adoptStats.pendingAdoptions(),
                adoptStats.totalAppointments(),
                adoptStats.ongoingAppointments());
        model.addAttribute("stats", adminStats);
        model.addAttribute("recentReviews", petClient.getRecentReviews());
        return "admin/dashboard";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        petClient.deleteReviewById(id);
        return "redirect:/admin/dashboard";
    }
}
