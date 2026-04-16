package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.adoption.dto.AdoptionCreateRequest;
import com.awbd.pawsy.adoption.dto.AppointmentCreateRequest;
import com.awbd.pawsy.adoption.service.AdoptionService;
import com.awbd.pawsy.adoption.service.AppointmentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import com.awbd.pawsy.pet.service.ShelterService;
import org.springframework.stereotype.Controller;
import org.springframework.data.domain.Pageable;
import com.awbd.pawsy.pet.dto.PetUpdateRequest;
import static java.util.Objects.requireNonNull;
import com.awbd.pawsy.user.service.UserService;
import com.awbd.pawsy.pet.dto.PetCreateRequest;
import com.awbd.pawsy.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import jakarta.validation.Valid;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {
    private final AppointmentService appointmentService;
    private final AdoptionService adoptionService;
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
            return "pets/create";
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

    @GetMapping("/{id}/edit")
    public String editPage(@PathVariable Long id, Model model) {
        var user = userService.getByUsername(requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName());
        var shelter = petService.getShelterForPet(id);
        if (!shelter.getManager().getId().equals(user.getId()))
            throw new AccessDeniedException("Editing other pets is not permitted.");

        var pet = petService.getForUpdate(id);
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
            petService.update(id, dto);
            redirect.addFlashAttribute("successMessage", "Pet updated successfully!");
            return "redirect:/pets/" + id;
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/pets/" + id;
        }
    }

    @PostMapping("/{id}/delete")
    public String deletePet(@PathVariable Long id, RedirectAttributes redirect) {
        petService.delete(id);
        redirect.addFlashAttribute("successMessage", "Pet deleted successfully!");
        return "redirect:/shelters/pets";
    }

    @GetMapping("/{id}/adopt")
    public String showAdoptionForm(@PathVariable Long id, Model model) {
        var pet = petService.summary(petService.get(id));
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
            model.addAttribute("pet", petService.summary(petService.get(id)));
            return "adoptions/create";
        }

        try {
            var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
            adoptionService.create(id, username, dto);
            redirect.addFlashAttribute("successMessage", "Your adoption request has been sent!");
            return "redirect:/pets/" + id;
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/pets/" + id;
        }
    }

    @GetMapping("/{id}/appointments/new")
    public String appointmentForm(@PathVariable Long id, Model model) {
        var pet = petService.summary(petService.get(id));
        var bookedDates = appointmentService.getBookedDates(id);

        model.addAttribute("pet", pet);
        model.addAttribute("appointment", new AppointmentCreateRequest(null));
        model.addAttribute("bookedDates", bookedDates
                .stream().map(LocalDate::toString).toList());
        return "appointments/create";
    }

    @PostMapping("/{id}/appointments")
    public String submitAppointment(@PathVariable Long id,
                                    @Valid @ModelAttribute("appointment") AppointmentCreateRequest dto,
                                    BindingResult result,
                                    RedirectAttributes redirect,
                                    Model model) {
        if (result.hasErrors()) {
            var pet = petService.summary(petService.get(id));
            model.addAttribute("pet", pet);
            model.addAttribute("bookedDates", appointmentService.getBookedDates(id)
                    .stream().map(LocalDate::toString).toList());
            return "appointments/create";
        }

        try {
            var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
            appointmentService.create(username, id, dto);
            redirect.addFlashAttribute("successMessage", "You scheduled an appointment!");
            return "redirect:/pets/" + id;
        } catch (IllegalStateException ise) {
            var pet = petService.summary(petService.get(id));
            model.addAttribute("pet", pet);
            model.addAttribute("bookedDates", appointmentService.getBookedDates(id)
                    .stream().map(LocalDate::toString).toList());
            model.addAttribute("errorMessage", ise.getMessage());
            return "appointments/create";
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Failed to schedule your appointment.");
            return "redirect:/pets/" + id;
        }
    }
}
