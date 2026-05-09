package com.awbd.pawsy.client;

import com.awbd.pawsy.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class PetClient {
    private final RestClient restClient;
    private final UserClient userClient;

    public PetClient(@Value("${pet.service.url}") String petServiceUrl, UserClient userClient) {
        log.info("PetClient registering at `{}`.", petServiceUrl);
        restClient = RestClient.builder()
                .baseUrl(petServiceUrl)
                .build();

        this.userClient = userClient;
    }

    public PageResponse<PetResponse> searchPets(String name, String species, String sex, Long shelterId, String sort, Integer page, Integer size) {
        final var type = new ParameterizedTypeReference<PageResponse<PetResponse>>() {};
        return restClient.get()
                .uri(uri -> uri.path("/pets")
                        .queryParam("name", name)
                        .queryParam("species", species)
                        .queryParam("sex", sex)
                        .queryParam("shelterId", shelterId)
                        .queryParam("sort", sort)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(type);
    }

    public PageResponse<ShelterResponse> searchShelters(String name, String location, String sort, Integer page, Integer size) {
        final var type = new ParameterizedTypeReference<PageResponse<ShelterResponse>>() {};
        return restClient.get()
                .uri(uri -> uri.path("/shelters/search")
                        .queryParam("name", name)
                        .queryParam("location", location)
                        .queryParam("sort", sort)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(type);
    }

    public Optional<ShelterResponse> getShelterByManager(String manager) {
        try {
            var response = restClient.get()
                    .uri("/shelters/by-manager/{manager}", manager)
                    .retrieve()
                    .body(ShelterResponse.class);

            return Optional.ofNullable(response);
        } catch (HttpClientErrorException.NotFound ignored) {
            return Optional.empty();
        }
    }

    public Optional<ShelterResponse> getShelterById(Long shelterId) {
        try {
            var response = restClient.get()
                    .uri("/shelters/{shelterId}", shelterId)
                    .retrieve()
                    .body(ShelterResponse.class);

            return Optional.ofNullable(response);
        } catch (HttpClientErrorException.NotFound ignored) {
            return Optional.empty();
        }
    }

    public void createShelter(ShelterCreateRequest dto, String manager) {
        var linked = new ShelterCreateRequest(dto.name(), dto.location(), dto.email(), dto.phone(), manager);
        restClient.post()
                .uri("/shelters")
                .body(linked)
                .retrieve()
                .toBodilessEntity();

        userClient.makeManager(manager);
    }

    public List<ShelterResponse> allShelters() {
        final var type = new ParameterizedTypeReference<List<ShelterResponse>>() {};
        return restClient.get()
                .uri("/shelters")
                .retrieve()
                .body(type);
    }

    public PageResponse<PetResponse> getPetsForShelterByManager(String username, Integer page, Integer size) {
        final var type = new ParameterizedTypeReference<PageResponse<PetResponse>>() {};
        return restClient.get()
                .uri(uri -> uri.path("/shelters/by-manager/%s/pets".formatted(username))
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(type);
    }

    public PageResponse<ReviewResponse> getReviewsForShelter(Long shelterId, Integer page, Integer size, String sort) {
        final var type = new ParameterizedTypeReference<PageResponse<ReviewResponse>>() {};
        return restClient.get()
                .uri(uri -> uri.path("/shelters/%d/reviews".formatted(shelterId))
                        .queryParam("sort", sort)
                        .queryParam("page", page)
                        .queryParam("size", size)
                        .build())
                .retrieve()
                .body(type);
    }

    public Optional<ReviewResponse> getReviewForUserAndShelter(String username, Long shelterId) {
        try {
            var response = restClient.get()
                    .uri("/reviews/for-user/{username}/at-shelter/{shelterId}", username, shelterId)
                    .retrieve()
                    .body(ReviewResponse.class);

            return Optional.ofNullable(response);
        } catch (HttpClientErrorException.NotFound ignored) {
            return Optional.empty();
        }
    }

    public void deleteReviewForUserAndShelter(String username, Long shelterId) {
        restClient.delete()
                .uri("/reviews/for-user/{username}/at-shelter/{shelterId}", username, shelterId)
                .retrieve()
                .toBodilessEntity();
    }

    public void createReviewForUserAndShelter(String username, Long shelterId, ReviewCreateRequest dto) {
        restClient.post()
                .uri("/reviews/for-user/{username}/at-shelter/{shelterId}", username, shelterId)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void editReviewForUserAndShelter(String username, Long shelterId, ReviewCreateRequest dto) {
        restClient.put()
                .uri("/reviews/for-user/{username}/at-shelter/{shelterId}", username, shelterId)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void deleteReviewById(Long reviewId) {
        restClient.delete()
                .uri("/reviews/{reviewId}", reviewId)
                .retrieve()
                .toBodilessEntity();
    }

    public List<ReviewResponse> getRecentReviews() {
        final var type = new ParameterizedTypeReference<List<ReviewResponse>>() {};
        return restClient.get()
                .uri("/reviews/recent")
                .retrieve()
                .body(type);
    }
}
