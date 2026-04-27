package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.dto.ReviewCreateRequest;
import com.awbd.pawsy.pet.dto.ReviewMapper;
import com.awbd.pawsy.pet.dto.ReviewSummary;
import com.awbd.pawsy.pet.model.Review;
import com.awbd.pawsy.pet.repository.ReviewRepository;
import com.awbd.pawsy.user.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ShelterService shelterService;
    private final ReviewMapper reviewMapper;
    private final UserService userService;

    public Optional<Review> getUserReview(Long adopterId, Long shelterId) {
        return reviewRepository.findByAdopterIdAndShelterId(adopterId, shelterId);
    }

    public List<ReviewSummary> getForShelter(Long shelterId) {
        return reviewRepository.findByShelterId(shelterId)
                .stream().map(reviewMapper::toSummary).toList();
    }

    public Page<ReviewSummary> getPageForShelter(Long shelterId, Integer page, Integer size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return reviewRepository.findByShelterId(shelterId, pageable)
                .map(reviewMapper::toSummary);
    }

    public void create(String username, Long shelterId, ReviewCreateRequest dto) {
        var user = userService.getByUsername(username);
        var shelter = shelterService.get(shelterId);
        var review = new Review();
        review.setAdopter(user);
        review.setShelter(shelter);
        review.setRating(dto.rating());
        review.setComment(dto.comment());
        review.setCreatedAt(LocalDateTime.now());
        review.setEditedAt(null);
        reviewRepository.save(review);
        log.info("Review by `{}` for shelter `{}` created.", username, shelterId);
    }

    public void edit(String username, Long shelterId, ReviewCreateRequest dto) {
        var user = userService.getByUsername(username);
        var review = reviewRepository.findByAdopterIdAndShelterId(user.getId(), shelterId)
                .orElseThrow(() -> new EntityNotFoundException("Review for adopter %s and shelter %d was not found!".formatted(username, shelterId)));
        review.setRating(dto.rating());
        review.setComment(dto.comment());
        review.setEditedAt(LocalDateTime.now());
        reviewRepository.save(review);
    }

    public void delete(Long adopterId, Long shelterId) {
        var review = reviewRepository
                .findByAdopterIdAndShelterId(adopterId, shelterId)
                .orElseThrow(() -> new EntityNotFoundException("Review for adopter %d and shelter %d was not found!".formatted(adopterId, shelterId)));
        reviewRepository.delete(review);
    }

    public List<ReviewSummary> getRecent() {
        return reviewRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(reviewMapper::toSummary)
                .toList();
    }

    public void deleteById(Long id) {
        if (!reviewRepository.existsById(id)) return;
        reviewRepository.deleteById(id);
    }
}
