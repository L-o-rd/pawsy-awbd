package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.dto.PageResponse;
import com.awbd.pawsy.pet.dto.PetUpdateRequest;
import com.awbd.pawsy.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{id}/update")
    public ResponseEntity<?> getForUpdate(@PathVariable Long id) {
        return ResponseEntity.ok().body(petService.getForUpdate(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        var pet = petService.getById(id);
        return pet.isPresent() ? ResponseEntity.ok().body(pet.get()) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody PetUpdateRequest dto) {
        petService.update(id, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        petService.delete(id);
        return ResponseEntity.ok().build();
    }
}
