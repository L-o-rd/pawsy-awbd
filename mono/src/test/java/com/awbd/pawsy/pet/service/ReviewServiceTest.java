package com.awbd.pawsy.pet.service;

import com.awbd.pawsy.pet.dto.ReviewCreateRequest;
import com.awbd.pawsy.pet.dto.ReviewMapper;
import com.awbd.pawsy.pet.dto.ReviewSummary;
import com.awbd.pawsy.pet.model.Review;
import com.awbd.pawsy.pet.model.Shelter;
import com.awbd.pawsy.pet.repository.ReviewRepository;
import com.awbd.pawsy.user.model.User;
import com.awbd.pawsy.user.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ShelterService shelterService;

    @Mock
    private ReviewMapper reviewMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private ReviewService reviewService;

    @Test
    public void whenReviewExists_getUserReview_getsTheReview() {
        when(reviewRepository.findByAdopterIdAndShelterId(1L, 1L)).thenReturn(Optional.of(new Review()));
        var result = reviewService.getUserReview(1L, 1L);
        assertNotNull(result);
        verify(reviewRepository).findByAdopterIdAndShelterId(1L, 1L);
    }

    @Test
    public void whenReviewsExist_getForShelter_getsTheReviews() {
        when(reviewRepository.findByShelterId(1L)).thenReturn(List.of(new Review()));
        when(reviewMapper.toSummary(any(Review.class))).thenReturn(new ReviewSummary(null, null, null, null, null, null));
        var all = reviewService.getForShelter(1L);
        assertEquals(1, all.size());
        verify(reviewRepository).findByShelterId(1L);
    }

    @Test
    public void whenReviewsExist_getPageForShelter_getsAPage() {
        final var page = new PageImpl<>(List.of(new Review()), PageRequest.of(0, 6), 1);
        when(reviewRepository.findByShelterId(eq(1L), any(Pageable.class))).thenReturn(page);
        when(reviewMapper.toSummary(any(Review.class))).thenReturn(new ReviewSummary(null, null, null, null, null, null));
        var result = reviewService.getPageForShelter(1L, 0, 6, "createdAt");
        assertEquals(1, result.getContent().size());
        verify(reviewRepository).findByShelterId(eq(1L), any(Pageable.class));
    }

    @Test
    public void always_create_createsAReview() {
        when(userService.getByUsername("user")).thenReturn(new User());
        when(shelterService.get(1L)).thenReturn(new Shelter());
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review());
        final var dto = new ReviewCreateRequest(null, null);
        reviewService.create("user", 1L, dto);
        verify(userService).getByUsername("user");
        verify(shelterService).get(1L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    public void whenReviewExists_edit_editsTheReview() {
        final var user = new User();
        user.setId(1L);
        when(userService.getByUsername("user")).thenReturn(user);
        when(reviewRepository.findByAdopterIdAndShelterId(1L, 1L)).thenReturn(Optional.of(new Review()));
        when(reviewRepository.save(any(Review.class))).thenReturn(new Review());
        reviewService.edit("user", 1L, new ReviewCreateRequest(null, null));
        verify(userService).getByUsername("user");
        verify(reviewRepository).findByAdopterIdAndShelterId(1L, 1L);
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    public void whenReviewExists_delete_deletesTheReview() {
        when(reviewRepository.findByAdopterIdAndShelterId(1L, 1L)).thenReturn(Optional.of(new Review()));
        doNothing().when(reviewRepository).delete(any(Review.class));
        reviewService.delete(1L, 1L);
        verify(reviewRepository).findByAdopterIdAndShelterId(1L, 1L);
        verify(reviewRepository).delete(any(Review.class));
    }

    @Test
    public void whenThereAreRecent_getRecent_getsTop5Reviews() {
        when(reviewRepository.findTop5ByOrderByCreatedAtDesc()).thenReturn(List.of());
        var all = reviewService.getRecent();
        assertEquals(0, all.size());
        verify(reviewRepository).findTop5ByOrderByCreatedAtDesc();
    }

    @Test
    public void whenReviewExists_deleteById_deletesTheReview() {
        when(reviewRepository.existsById(1L)).thenReturn(true);
        doNothing().when(reviewRepository).deleteById(1L);
        reviewService.deleteById(1L);
        verify(reviewRepository).existsById(1L);
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    public void whenReviewDoesntExist_deleteById_doesNothing() {
        when(reviewRepository.existsById(1L)).thenReturn(false);
        reviewService.deleteById(1L);
        verify(reviewRepository).existsById(1L);
        verify(reviewRepository, times(0)).deleteById(any(Long.class));
    }
}