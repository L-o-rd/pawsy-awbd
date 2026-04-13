package com.awbd.pawsy.user.controller;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import com.awbd.pawsy.user.dto.UserCreateRequest;
import com.awbd.pawsy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
@RequestMapping("/register")
public class AuthController {
    private final UserService userService;

    @GetMapping
    public String register(Model model) {
        model.addAttribute("user", new UserCreateRequest(null, null, null, null, null, null));
        return "register";
    }

    @PostMapping
    public String registerUser(@Valid @ModelAttribute("user") UserCreateRequest user,
                               BindingResult result,
                               RedirectAttributes at) {
        if (result.hasErrors()) {
            return "register";
        }

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
