package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.dto.PageResponse;
import com.awbd.pawsy.pet.dto.ShelterCreateRequest;
import com.awbd.pawsy.pet.service.PetService;
import com.awbd.pawsy.pet.service.ReviewService;
import com.awbd.pawsy.pet.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/shelters")
public class ShelterController {
    private final ShelterService shelterService;
    private final ReviewService reviewService;
    private final PetService petService;

    @GetMapping
    public ResponseEntity<?> all() {
        return ResponseEntity.ok().body(shelterService.all());
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String name,
                                    @RequestParam(required = false) String location,
                                    @RequestParam(defaultValue = "name") String sort,
                                    @RequestParam(defaultValue = "0") Integer page,
                                    @RequestParam(defaultValue = "12") Integer size) {

        var sheltersPage = shelterService.search(name, location, sort, page, size);
        return ResponseEntity.ok().body(PageResponse.collect(sheltersPage));
    }

    @GetMapping("/by-manager/{manager}")
    public ResponseEntity<?> getByManager(@PathVariable String manager) {
        var shelter = shelterService.getByManager(manager);
        return shelter.isPresent() ? ResponseEntity.ok().body(shelter) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getByManager(@PathVariable Long id) {
        var shelter = shelterService.getById(id);
        return shelter.isPresent() ? ResponseEntity.ok().body(shelter.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/by-manager/{manager}/pets")
    public ResponseEntity<?> getPetsByManager(@PathVariable String manager,
                                              @RequestParam(defaultValue = "0") Integer page,
                                              @RequestParam(defaultValue = "12") Integer size) {
        var petsPage = petService.getPetsByManager(manager, page, size);
        return ResponseEntity.ok().body(PageResponse.collect(petsPage));
    }

    @GetMapping("/{id}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable Long id,
                                        @RequestParam(defaultValue = "0") Integer page,
                                        @RequestParam(defaultValue = "12") Integer size,
                                        @RequestParam(defaultValue = "createdAt") String sort) {
        var reviewsPage = reviewService.getPageForShelter(id, page, size, sort);
        return ResponseEntity.ok().body(PageResponse.collect(reviewsPage));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ShelterCreateRequest dto) {
        var shelter = shelterService.create(dto);
        return ResponseEntity.created(URI.create("/shelters/%d".formatted(shelter.getId()))).build();
    }
}
