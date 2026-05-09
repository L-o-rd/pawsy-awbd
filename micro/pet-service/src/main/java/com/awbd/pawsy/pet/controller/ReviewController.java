package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.dto.ReviewCreateRequest;
import com.awbd.pawsy.pet.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/for-user/{username}/at-shelter/{shelterId}")
    public ResponseEntity<?> getForUserAndShelter(@PathVariable String username, @PathVariable Long shelterId) {
        var review = reviewService.getUserReview(username, shelterId);
        return review.isPresent() ? ResponseEntity.ok().body(review.get()) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/for-user/{username}/at-shelter/{shelterId}")
    public ResponseEntity<?> deleteForUserAndShelter(@PathVariable String username, @PathVariable Long shelterId) {
        reviewService.deleteUserReview(username, shelterId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteById(@PathVariable Long id) {
        reviewService.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/for-user/{username}/at-shelter/{shelterId}")
    public ResponseEntity<?> createForUserAndShelter(@PathVariable String username, @PathVariable Long shelterId,
                                                     @RequestBody ReviewCreateRequest dto) {
        reviewService.createUserReview(username, shelterId, dto);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/for-user/{username}/at-shelter/{shelterId}")
    public ResponseEntity<?> editForUserAndShelter(@PathVariable String username, @PathVariable Long shelterId,
                                                     @RequestBody ReviewCreateRequest dto) {
        reviewService.editUserReview(username, shelterId, dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent")
    public ResponseEntity<?> recent() {
        return ResponseEntity.ok().body(reviewService.getRecent());
    }
}
