package com.awbd.pawsy.pet.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import com.awbd.pawsy.pet.service.ShelterService;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Pageable;
import com.awbd.pawsy.user.service.UserService;
import com.awbd.pawsy.pet.dto.PetCreateRequest;
import com.awbd.pawsy.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import jakarta.validation.Valid;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {
    private final ShelterService shelterService;
    private final UserService userService;
    private final PetService petService;

    @GetMapping
    public String all(@RequestParam(required=false) String name,
                      @RequestParam(required=false) String species,
                      @RequestParam(required=false) String sex,
                      @RequestParam(required=false) Long shelter,
                      @RequestParam(defaultValue="name") String sort,
                      @PageableDefault(size = 12) Pageable pageable, Model model) {
        var pets = petService.search(name, species, sex, shelter, sort, pageable);
        model.addAttribute("shelters", shelterService.all());
        model.addAttribute("petsPage", pets);
        return "pets/list";
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable Long id, Model model) {
        var pet = petService.get(id);
        var relatedPets = petService.related(id);
        model.addAttribute("relatedPets", relatedPets);
        model.addAttribute("pet", petService.summary(pet));
        model.addAttribute("shelterName", pet.getShelter().getName());
        return "pets/profile";
    }

    @GetMapping("/create")
    public String createPage(Model model) {
        model.addAttribute("pet", new PetCreateRequest(null, null, null, null, null, null));
        return "pets/create";
    }

    @PostMapping("/create")
    public String createPet(@Valid @ModelAttribute("pet") PetCreateRequest dto,
                            BindingResult result,
                            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            redirect.addFlashAttribute("errorMessage", result.getAllErrors().toString());
            return "redirect:pets/create";
        }

        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null) {
                throw new RuntimeException("User not logged in.");
            }

            var user = userService.getByUsername(auth.getName());
            var createdPet = petService.create(dto, shelterService.getByManager(user));
            redirect.addFlashAttribute("successMessage", "Pet added successfully!");
            return "redirect:/pets/%d".formatted(createdPet.getId());
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/pets/create";
        }
    }
}
