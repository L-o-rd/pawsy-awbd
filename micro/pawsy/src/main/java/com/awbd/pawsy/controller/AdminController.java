package com.awbd.pawsy.controller;

import com.awbd.pawsy.client.PetClient;
import com.awbd.pawsy.client.UserClient;
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
    private final UserClient userClient;
    private final PetClient petClient;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", userClient.getAdminStats());
        model.addAttribute("recentReviews", petClient.getRecentReviews());
        return "admin/dashboard";
    }

    @PostMapping("/reviews/{id}/delete")
    public String deleteReview(@PathVariable Long id) {
        petClient.deleteReviewById(id);
        return "redirect:/admin/dashboard";
    }
}
