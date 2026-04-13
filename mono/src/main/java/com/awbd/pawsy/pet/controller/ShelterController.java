package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.pet.service.ShelterService;
import com.awbd.pawsy.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static java.util.Objects.requireNonNull;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shelters")
public class ShelterController {
    private final ShelterService shelterService;
    private final UserService userService;
    private final PetService petService;

    @GetMapping("/pets")
    @PreAuthorize("hasRole('MANAGER')")
    public String myPets(@RequestParam(defaultValue = "0") Integer page,
                         @RequestParam(defaultValue = "6") Integer size,
                         Model model) {
        var auth = requireNonNull(SecurityContextHolder.getContext().getAuthentication());
        var user = userService.getByUsername(auth.getName());
        var petsPage = petService.getPetsForShelter(shelterService.getByManager(user), page, size);
        model.addAttribute("petsPage", petsPage);
        return "shelters/pets";
    }
}
