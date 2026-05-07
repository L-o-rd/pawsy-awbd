package com.awbd.pawsy.controller;

import com.awbd.pawsy.client.UserClient;
import com.awbd.pawsy.dto.UserCreateRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("")
@RequiredArgsConstructor
public class AuthController {
    private final UserClient userClient;

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("user", new UserCreateRequest(null, null, null, null, null, null));
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserCreateRequest user,
                               BindingResult result,
                               RedirectAttributes at) {
        if (result.hasErrors()) {
            return "register";
        }

        try {
            userClient.registerUser(user);
            at.addFlashAttribute("successMessage", "Registration successful. You can now login.");
            return "redirect:/register";
        } catch (Exception e) {
            at.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
