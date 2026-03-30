package com.awbd.pawsy.pet.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;
import com.awbd.pawsy.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {
    private final PetService petService;

    @GetMapping
    public String all(@RequestParam(required = false) Map<String, String> filters, Model model) {
        model.addAttribute("pets", petService.all(filters));
        model.addAttribute("filters", filters);
        return "pets/list";
    }
}
