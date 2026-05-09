package com.awbd.pawsy.controller;

import com.awbd.pawsy.client.PetClient;
import com.awbd.pawsy.dto.ShelterCreateRequest;
import com.awbd.pawsy.security.ContextUtils;
import com.awbd.pawsy.security.PawsyUserDetailsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
}
