package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.dto.PageResponse;
import com.awbd.pawsy.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pets")
public class PetController {
    private final PetService petService;

    @GetMapping
    public ResponseEntity<?> search(@RequestParam(required=false) String name,
                                 @RequestParam(required=false) String species,
                                 @RequestParam(required=false) String sex,
                                 @RequestParam(required=false) Long shelter,
                                 @RequestParam(defaultValue="name") String sort,
                                 @RequestParam(defaultValue="0") Integer page,
                                 @RequestParam(defaultValue="12") Integer size) {
        return ResponseEntity.ok().body(PageResponse.collect(petService.search(name, species, sex, shelter, sort, page, size)));
    }
}
