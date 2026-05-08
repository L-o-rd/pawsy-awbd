package com.awbd.pawsy.controller;

import com.awbd.pawsy.client.PetClient;
import com.awbd.pawsy.client.UserClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {
    private final UserClient userClient;
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
}
