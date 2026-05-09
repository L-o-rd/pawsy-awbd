package com.awbd.pawsy.controller;

import com.awbd.pawsy.client.AdoptionClient;
import com.awbd.pawsy.client.PetClient;
import com.awbd.pawsy.dto.ReviewCreateRequest;
import com.awbd.pawsy.dto.ShelterCreateRequest;
import com.awbd.pawsy.security.ContextUtils;
import com.awbd.pawsy.security.PawsyUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shelters")
public class ShelterController {
    private final PawsyUserDetailsService pawsyUserDetailsService;
    private final AdoptionClient adoptionClient;
    private final PetClient petClient;

    @GetMapping
    public String all(@RequestParam(required = false) String name,
                      @RequestParam(required = false) String location,
                      @RequestParam(defaultValue = "name") String sort,
                      @RequestParam(defaultValue = "0") Integer page,
                      @RequestParam(defaultValue = "12") Integer size,
                      Model model) {

        var sheltersPage = petClient.searchShelters(name, location, sort, page, size);
        model.addAttribute("sheltersPage", sheltersPage);
        model.addAttribute("name", name);
        model.addAttribute("location", location);
        model.addAttribute("sort", sort);
        return "shelters/list";
    }

    @GetMapping("/pets")
    public String myPets(@RequestParam(defaultValue = "0") Integer page,
                         @RequestParam(defaultValue = "6") Integer size,
                         Model model) {
        var petsPage = petClient.getPetsForShelterByManager(ContextUtils.getCurrentUsername(), page, size);
        model.addAttribute("petsPage", petsPage);
        return "shelters/pets";
    }

    @GetMapping("/apply")
    public String showApplyForm(Model model) {
        model.addAttribute("shelter", new ShelterCreateRequest(null, null, null, null, null));
        return "shelters/apply";
    }

    @PostMapping("/apply")
    public String apply(@Valid @ModelAttribute("shelter") ShelterCreateRequest dto,
                        BindingResult result,
                        RedirectAttributes redirect) {

        if (result.hasErrors()) {
            return "shelters/apply";
        }

        try {
            var username = ContextUtils.getCurrentUsername();
            var shelter = petClient.getShelterByManager(username);
            if (shelter.isPresent()) {
                redirect.addFlashAttribute("errorMessage", "You are already a manager for `%s`.".formatted(shelter.get().name()));
                return "redirect:/shelters/apply";
            }

            petClient.createShelter(dto, username);
            var updated = pawsyUserDetailsService.loadUserByUsername(username);
            var auth = new UsernamePasswordAuthenticationToken(updated, updated.getPassword(), updated.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            redirect.addFlashAttribute("successMessage", "Your shelter has been registered successfully!");
            return "redirect:/shelters/pets";
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/shelters/apply";
        }
    }

    @GetMapping("/{id}")
    public String shelterDetails(@PathVariable Long id,
                                 @RequestParam(defaultValue = "0") Integer page,
                                 @RequestParam(defaultValue = "6") Integer size,
                                 @RequestParam(defaultValue = "createdAt") String sort,
                                 Model model) {
        var shelter = petClient.getShelterById(id).orElseThrow(() -> new RuntimeException("No shelter in details?"));
        var petsPage = petClient.getPetsForShelterByManager(shelter.manager(), page, size);
        var reviewsPage = petClient.getReviewsForShelter(id, page, size, sort);
        var userReview = petClient.getReviewForUserAndShelter(ContextUtils.getCurrentUsername(), id);

        model.addAttribute("petsPage", petsPage);
        model.addAttribute("shelter", shelter);
        model.addAttribute("reviewsPage", reviewsPage);

        model.addAttribute("reviewForm", new ReviewCreateRequest(null, null));
        model.addAttribute("userReview", userReview.orElse(null));
        return "shelters/profile";
    }

    @PostMapping("/{id}/review/delete")
    public String deleteReview(@PathVariable Long id) {
        petClient.deleteReviewForUserAndShelter(ContextUtils.getCurrentUsername(), id);
        return "redirect:/shelters/" + id;
    }

    @PostMapping("/{id}/review")
    public String submitReview(@PathVariable Long id,
                               @Valid @ModelAttribute("reviewForm") ReviewCreateRequest dto,
                               BindingResult result,
                               RedirectAttributes redirect) {

        var username = ContextUtils.getCurrentUsername();
        var userReview = petClient.getReviewForUserAndShelter(username, id);
        if (result.hasErrors()) {
            redirect.addFlashAttribute("errorMessage", "Reviews should be at least 10 characters.");
            return "redirect:/shelters/" + id;
        }

        try {
            if (userReview.isEmpty()) {
                petClient.createReviewForUserAndShelter(username, id, dto);
                redirect.addFlashAttribute("successMessage", "Review created!");
            } else {
                petClient.editReviewForUserAndShelter(username, id, dto);
                redirect.addFlashAttribute("successMessage", "Review edited!");
            }

            return "redirect:/shelters/" + id;
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Failed to submit review.");
            return "redirect:/shelters/" + id;
        }
    }

    @GetMapping("/adoptions")
    public String adoptionRequests(Model model) {
        var shelter = petClient.getShelterByManager(ContextUtils.getCurrentUsername()).orElseThrow(() -> new RuntimeException("No shelter in adoption requests?"));
        var requests = adoptionClient.getRequestsForShelter(shelter.id());
        model.addAttribute("requests", requests);
        return "shelters/adoptions";
    }

    @PostMapping("/adoptions/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes at) {
        try {
            var adoption = adoptionClient.getById(id).orElseThrow(() -> new RuntimeException("No adoption in approve?"));
            var shelter = petClient.getShelterById(adoption.pet().shelterId()).orElseThrow(() -> new RuntimeException("No shelter in approve?"));
            if (!shelter.manager().equals(ContextUtils.getCurrentUsername()))
                throw new AccessDeniedException("You are not the manager!");

            adoptionClient.approveRequest(id);
            at.addFlashAttribute("successMessage", "You have approved a request.");
            return "redirect:/shelters/adoptions";
        } catch (Exception e) {
            at.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/shelters/adoptions";
        }
    }

    @PostMapping("/adoptions/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes at) {
        try {
            var adoption = adoptionClient.getById(id).orElseThrow(() -> new RuntimeException("No adoption in reject?"));
            var shelter = petClient.getShelterById(adoption.pet().shelterId()).orElseThrow(() -> new RuntimeException("No shelter in reject?"));
            if (!shelter.manager().equals(ContextUtils.getCurrentUsername()))
                throw new AccessDeniedException("You are not the manager!");

            adoptionClient.rejectRequest(id);
            at.addFlashAttribute("successMessage", "You have rejected a request.");
            return "redirect:/shelters/adoptions";
        } catch (Exception e) {
            at.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/shelters/adoptions";
        }
    }
}
