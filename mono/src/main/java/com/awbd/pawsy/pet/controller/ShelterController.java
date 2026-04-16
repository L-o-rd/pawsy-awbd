package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.adoption.service.AdoptionService;
import com.awbd.pawsy.pet.dto.ReviewCreateRequest;
import com.awbd.pawsy.pet.dto.ShelterCreateRequest;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.pet.service.ReviewService;
import com.awbd.pawsy.pet.service.ShelterService;
import com.awbd.pawsy.security.PawsyUserDetailsService;
import com.awbd.pawsy.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
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

import static java.util.Objects.requireNonNull;

@Controller
@RequiredArgsConstructor
@RequestMapping("/shelters")
public class ShelterController {
    private final PawsyUserDetailsService pawsyUserDetailsService;
    private final AdoptionService adoptionService;
    private final ShelterService shelterService;
    private final ReviewService reviewService;
    private final UserService userService;
    private final PetService petService;

    @GetMapping("/pets")
    public String myPets(@RequestParam(defaultValue = "0") Integer page,
                         @RequestParam(defaultValue = "6") Integer size,
                         Model model) {
        var auth = requireNonNull(SecurityContextHolder.getContext().getAuthentication());
        var user = userService.getByUsername(auth.getName());
        var petsPage = petService.getPetsForShelter(shelterService.getByManager(user), page, size);
        model.addAttribute("petsPage", petsPage);
        return "shelters/pets";
    }

    @GetMapping
    public String all(@RequestParam(required = false) String name,
                       @RequestParam(required = false) String location,
                       @RequestParam(defaultValue = "name") String sort,
                       @RequestParam(defaultValue = "0") Integer page,
                       @RequestParam(defaultValue = "12") Integer size,
                       Model model) {

        var sheltersPage = shelterService.search(name, location, sort, page, size);
        model.addAttribute("sheltersPage", sheltersPage);
        model.addAttribute("name", name);
        model.addAttribute("location", location);
        model.addAttribute("sort", sort);
        return "shelters/list";
    }

    @GetMapping("/apply")
    public String showApplyForm(Model model) {
        model.addAttribute("shelter", new ShelterCreateRequest(null, null, null, null));
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
            var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
            var user = userService.getByUsername(username);

            try {
                var shelter = shelterService.getByManager(user);
                redirect.addFlashAttribute("errorMessage", "You are already a manager for `%s`.".formatted(shelter.getName()));
                return "redirect:/shelters/apply";
            } catch (EntityNotFoundException ignored) {}

            shelterService.create(dto, user);
            var updated = pawsyUserDetailsService.loadUserByUsername(username);
            var uauth = new UsernamePasswordAuthenticationToken(updated, updated.getPassword(), updated.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(uauth);
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

        var shelter = shelterService.summary(id);
        var petsPage = petService.getPetsForShelter(shelterService.get(id), page, size);
        var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var user = userService.getByUsername(username);
        var reviewsPage = reviewService.getPageForShelter(id, page, size, sort);
        var userReview = reviewService.getUserReview(user.getId(), id);

        model.addAttribute("petsPage", petsPage);
        model.addAttribute("shelter", shelter);
        model.addAttribute("reviewsPage", reviewsPage);

        model.addAttribute("reviewForm", new ReviewCreateRequest(null, null));
        model.addAttribute("userReview", userReview.orElse(null));
        return "shelters/profile";
    }

    @PostMapping("/{id}/review/delete")
    public String deleteReview(@PathVariable Long id) {
        var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var user = userService.getByUsername(username);
        reviewService.delete(user.getId(), id);
        return "redirect:/shelters/" + id;
    }

    @PostMapping("/{id}/review")
    public String submitReview(@PathVariable Long id,
                               @Valid @ModelAttribute("reviewForm") ReviewCreateRequest dto,
                               BindingResult result,
                               RedirectAttributes redirect) {

        var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var user = userService.getByUsername(username);
        var userReview = reviewService.getUserReview(user.getId(), id);
        if (result.hasErrors()) {
            redirect.addFlashAttribute("errorMessage", "Reviews should be at least 10 characters.");
            return "redirect:/shelters/" + id;
        }

        try {
            if (userReview.isEmpty()) {
                redirect.addFlashAttribute("successMessage", "Review created!");
                reviewService.create(username, id, dto);
            } else {
                redirect.addFlashAttribute("successMessage", "Review edited!");
                reviewService.edit(username, id, dto);
            }

            return "redirect:/shelters/" + id;
        } catch (Exception e) {
            redirect.addFlashAttribute("errorMessage", "Failed to submit review.");
            return "redirect:/shelters/" + id;
        }
    }

    @GetMapping("/adoptions")
    public String adoptionRequests(Model model) {
        var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
        var user = userService.getByUsername(username);
        var shelter = shelterService.getByManager(user);
        var requests = adoptionService.getRequestsForShelter(shelter.getId());
        model.addAttribute("requests", requests);
        return "shelters/adoptions";
    }

    @PostMapping("/adoptions/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes at) {
        try {
            var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
            var user = userService.getByUsername(username);
            var adoption = adoptionService.get(id);

            if (!adoption.getPet().getShelter().getManager().getId().equals(user.getId())) {
                throw new AccessDeniedException("You are not the manager!");
            }

            adoptionService.approve(id);
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
            var username = requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
            var user = userService.getByUsername(username);
            var adoption = adoptionService.get(id);

            if (!adoption.getPet().getShelter().getManager().getId().equals(user.getId())) {
                throw new AccessDeniedException("You are not the manager!");
            }

            adoptionService.reject(id);
            at.addFlashAttribute("successMessage", "You have rejected a request.");
            return "redirect:/shelters/adoptions";
        } catch (Exception e) {
            at.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/shelters/adoptions";
        }
    }
}
