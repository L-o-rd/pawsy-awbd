package com.awbd.pawsy.controller;

import com.awbd.pawsy.client.AdoptionClient;
import com.awbd.pawsy.client.PetClient;
import com.awbd.pawsy.dto.AdoptionCreateRequest;
import com.awbd.pawsy.dto.PetCreateRequest;
import com.awbd.pawsy.dto.PetUpdateRequest;
import com.awbd.pawsy.exception.ResourceNotFoundException;
import com.awbd.pawsy.security.ContextUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {
    private final AdoptionClient adoptionClient;
    private final PetClient petClient;

    @GetMapping
    public String all(@RequestParam(required = false) String name,
                      @RequestParam(required = false) String species,
                      @RequestParam(required = false) String sex,
                      @RequestParam(required = false) Long shelter,
                      @RequestParam(defaultValue = "name") String sort,
                      @RequestParam(defaultValue = "0") Integer page,
                      @RequestParam(defaultValue = "12") Integer size, Model model) {
        var pets = petClient.searchPets(name, species, sex, shelter, sort, page, size);
        model.addAttribute("shelters", petClient.allShelters());
        model.addAttribute("petsPage", pets);
        return "pets/list";
    }

    @GetMapping("/{id}")
    public String profile(@PathVariable Long id, Model model) {
        var pet = petClient.getPetById(id).orElseThrow(() -> new ResourceNotFoundException("Pet not found."));
        var relatedPets = petClient.getRelatedPets(id);
        var shelter = petClient.getShelterById(pet.shelterId()).orElseThrow(() -> new RuntimeException("No shelter in pet profile?"));

        model.addAttribute("pet", pet);
        model.addAttribute("relatedPets", relatedPets);
        model.addAttribute("shelterName", shelter.name());
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
            return "pets/create";
        }

        try {
            var shelter = petClient.getShelterByManager(ContextUtils.getCurrentUsername()).orElseThrow(() -> new RuntimeException("You are not a manager."));
            var createdPet = petClient.createPet(dto, shelter.id());
            redirect.addFlashAttribute("successMessage", "Pet added successfully!");
            return "redirect:/pets/%d".formatted(createdPet.id());
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/pets/create";
        }
    }

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        var vpet = petClient.getPetById(id).orElseThrow(() -> new ResourceNotFoundException("No pet in edit."));
        var shelter = petClient.getShelterById(vpet.shelterId()).orElseThrow(() -> new RuntimeException("No shelter for pet in edit?"));
        if (!shelter.manager().equals(ContextUtils.getCurrentUsername()))
            throw new AccessDeniedException("Editing other pets is not permitted.");

        var pet = petClient.getPetForUpdate(id);
        model.addAttribute("petId", id);
        model.addAttribute("pet", pet);
        return "pets/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePet(@PathVariable Long id,
                            @Valid @ModelAttribute("pet") PetUpdateRequest dto,
                            BindingResult result,
                            Model model,
                            RedirectAttributes redirect) {

        if (result.hasErrors()) {
            model.addAttribute("petId", id);
            return "pets/edit";
        }

        try {
            petClient.updatePet(id, dto);
            redirect.addFlashAttribute("successMessage", "Pet updated successfully!");
            return "redirect:/pets/" + id;
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/pets/" + id;
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePet(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            petClient.deletePet(id);
            redirect.addFlashAttribute("successMessage", "Pet deleted successfully!");
            return "redirect:/shelters/pets";
        } catch (Exception e) {
            log.error("Failed to delete pet `{}`.", id, e);
            redirect.addFlashAttribute("errorMessage", "Failed to delete pet.");
            return "redirect:/shelters/pets";
        }
    }

    @GetMapping("/{id}/adopt")
    public String showAdoptionForm(@PathVariable Long id, Model model) {
        var pet = petClient.getPetById(id).orElseThrow(() -> new RuntimeException("No such pet."));
        model.addAttribute("adoption", new AdoptionCreateRequest(null));
        model.addAttribute("pet", pet);
        return "adoptions/create";
    }

    @PostMapping("/{id}/adopt")
    public String submitAdoption(@PathVariable Long id,
                                 @Valid @ModelAttribute("adoption") AdoptionCreateRequest dto,
                                 BindingResult result,
                                 RedirectAttributes redirect,
                                 Model model) {
        if (result.hasErrors()) {
            model.addAttribute("pet", petClient.getPetById(id).orElseThrow(() -> new RuntimeException("No such pet.")));
            return "adoptions/create";
        }

        try {
            final var pet = petClient.getPetById(id).orElseThrow(() -> new RuntimeException("No such pet."));
            var username = ContextUtils.getCurrentUsername();
            adoptionClient.create(id, pet.shelterId(), username, dto);
            redirect.addFlashAttribute("successMessage", "Your adoption request has been sent!");
            return "redirect:/pets/" + id;
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/pets/" + id;
        }
    }
}
