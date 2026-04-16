package com.awbd.pawsy.pet.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.awbd.pawsy.pet.model.Review;
import java.util.Optional;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByAdopterIdAndShelterId(Long adopterId, Long shelterId);
    Page<Review> findByShelterId(Long shelterId, Pageable pageable);
    List<Review> findByShelterId(Long shelterId);
}
