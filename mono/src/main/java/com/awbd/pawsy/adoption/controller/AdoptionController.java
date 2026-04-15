package com.awbd.pawsy.adoption.controller;

import com.awbd.pawsy.adoption.service.AdoptionService;
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
@RequestMapping("/adoptions")
public class AdoptionController {
    private final AdoptionService adoptionService;
    private final UserService userService;

    @GetMapping("/my")
    public String myRequests(Model model) {
        var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var user = userService.getByUsername(username);
        var requests = adoptionService.getRequestsForAdopter(user.getId());
        model.addAttribute("requests", requests);
        return "adoptions/my";
    }
}
