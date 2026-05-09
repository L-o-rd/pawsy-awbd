package com.awbd.pawsy.pet.controller;

import com.awbd.pawsy.pet.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
