package com.awbd.pawsy.user.controller;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import com.awbd.pawsy.user.service.UserService;
import com.awbd.pawsy.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class AuthController {
    private final UserService userService;

    @GetMapping
    public String register(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String registerUser(@ModelAttribute User user, RedirectAttributes at) {
        try {
            userService.registerUser(user);
            at.addFlashAttribute("successMessage", "Registration successful. You can now login.");
            return "redirect:/register";
        } catch (Exception e) {
            at.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/register";
        }
    }
}
