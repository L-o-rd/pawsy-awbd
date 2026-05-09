package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.dto.ReviewMapper;
import com.awbd.pawsy.pet.dto.ReviewSummary;
import com.awbd.pawsy.pet.repository.ReviewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewSummary> getForShelter(Long shelterId) {
        return reviewRepository.findByShelterId(shelterId)
                .stream().map(reviewMapper::toSummary).toList();
    }

    public Page<ReviewSummary> getPageForShelter(Long shelterId, Integer page, Integer size, String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return reviewRepository.findByShelterId(shelterId, pageable)
                .map(reviewMapper::toSummary);
    }

    public Optional<ReviewSummary> getUserReview(String adopter, Long shelterId) {
        return reviewRepository.findByAdopterAndShelterId(adopter, shelterId).map(reviewMapper::toSummary);
    }
}
