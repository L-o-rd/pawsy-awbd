package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.dto.PageResponse;
import com.awbd.pawsy.pet.dto.ShelterCreateRequest;
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

    @PostMapping
    public ResponseEntity<?> create(@RequestBody ShelterCreateRequest dto) {
        var shelter = shelterService.create(dto);
        return ResponseEntity.created(URI.create("/shelters/%d".formatted(shelter.getId()))).build();
    }
}
