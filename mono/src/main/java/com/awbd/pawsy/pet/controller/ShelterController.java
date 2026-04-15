package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.dto.ShelterCreateRequest;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.pet.service.ShelterService;
import com.awbd.pawsy.security.PawsyUserDetailsService;
import com.awbd.pawsy.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    private final ShelterService shelterService;
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
                                 Model model) {

        var shelter = shelterService.summary(id);
        var petsPage = petService.getPetsForShelter(shelterService.get(id), page, size);

        model.addAttribute("petsPage", petsPage);
        model.addAttribute("shelter", shelter);
        return "shelters/profile";
    }
}
