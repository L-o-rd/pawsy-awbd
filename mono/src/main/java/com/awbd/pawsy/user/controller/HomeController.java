package com.awbd.pawsy.user.controller;

import com.awbd.pawsy.user.dto.UserUpdateRequest;
import com.awbd.pawsy.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import static java.util.Objects.requireNonNull;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final UserService userService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/profile")
    public String myProfile(Model model) {
        var auth = requireNonNull(SecurityContextHolder.getContext().getAuthentication());
        var user = userService.getByUsername(auth.getName());
        model.addAttribute("user", userService.getProfileForUpdate(user));
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@Valid @ModelAttribute("user") UserUpdateRequest dto,
                                BindingResult result,
                                RedirectAttributes redirect) {

        if (result.hasErrors()) {
            return "profile";
        }

        var auth = requireNonNull(SecurityContextHolder.getContext().getAuthentication());
        userService.update(auth.getName(), dto);
        redirect.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile";
    }
}
