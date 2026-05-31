package com.awbd.pawsy.controller;

import com.awbd.pawsy.client.AdoptionClient;
import com.awbd.pawsy.security.ContextUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/adoptions")
public class AdoptionController {
    private final AdoptionClient adoptionClient;

    @GetMapping("/my")
    public String myRequests(Model model) {
        var username = ContextUtils.getCurrentUsername();
        var requests = adoptionClient.getRequestsForAdopter(username);
        model.addAttribute("requests", requests);
        return "adoptions/my";
    }
}
