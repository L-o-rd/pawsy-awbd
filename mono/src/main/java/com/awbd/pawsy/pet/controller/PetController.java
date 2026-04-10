package com.awbd.pawsy.pet.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.data.web.PageableDefault;
import com.awbd.pawsy.pet.service.ShelterService;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Pageable;
import com.awbd.pawsy.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {
    private final ShelterService shelterService;
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
        var relatedPets = petService.related(pet);
        model.addAttribute("relatedPets", relatedPets);
        model.addAttribute("pet", petService.summary(pet));
        model.addAttribute("shelterName", pet.getShelter().getName());
        return "pets/profile";
    }
}
