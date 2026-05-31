package com.awbd.pawsy.client;

import com.awbd.pawsy.dto.AdoptionCreateRequest;
import com.awbd.pawsy.dto.AdoptionResponse;
import com.awbd.pawsy.dto.AdoptionSummary;
import com.awbd.pawsy.dto.PetResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Slf4j
@Component
public class AdoptionClient {
    private final RestClient restClient;
    private final PetClient petClient;

    public AdoptionClient(@Value("${adoption.service.url}") String adoptionServiceUrl, PetClient petClient) {
        log.info("AdoptionClient registering at `{}`.", adoptionServiceUrl);
        restClient = RestClient.builder()
                .baseUrl(adoptionServiceUrl)
                .build();

        this.petClient = petClient;
    }

    public List<AdoptionResponse> getRequestsForShelter(Long shelterId) {
        final var type = new ParameterizedTypeReference<List<AdoptionSummary>>() {};
        var vass = restClient.get()
                .uri("/adoptions/by-shelter/{shelterId}", shelterId)
                .retrieve()
                .body(type);

        final var ass = requireNonNull(vass);
        return ass.stream().map(as -> new AdoptionResponse(as.id(),
                petClient.getPetById(as.petId()).orElseThrow(),
                as.adopterName(),
                as.requestDate(),
                as.status())).toList();
    }

    public List<AdoptionResponse> getRequestsForAdopter(final String username) {
        final var type = new ParameterizedTypeReference<List<AdoptionSummary>>() {};
        var vass = restClient.get()
                .uri("/adoptions/by-user/{username}", username)
                .retrieve()
                .body(type);

        final var ass = requireNonNull(vass);
        return ass.stream().map(as -> new AdoptionResponse(as.id(),
                petClient.getPetById(as.petId()).orElseThrow(),
                as.adopterName(),
                as.requestDate(),
                as.status())).toList();
    }

    public Optional<AdoptionResponse> getById(Long adoptionId) {
        try {
            var vas = restClient.get()
                    .uri("/adoptions/{adoptionId}", adoptionId)
                    .retrieve()
                    .body(AdoptionSummary.class);

            final var as = requireNonNull(vas);
            return Optional.of(new AdoptionResponse(as.id(),
                    petClient.getPetById(as.petId()).orElseThrow(),
                    as.adopterName(),
                    as.requestDate(),
                    as.status()));
        } catch (HttpClientErrorException.NotFound ignored) {
            return Optional.empty();
        }
    }

    public void create(Long petId, Long shelterId, String username, AdoptionCreateRequest dto) {
        restClient.post()
                .uri("/adoptions/for-pet/{petId}/at/{shelterId}/for-user/{username}",
                            petId, shelterId, username)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void approveRequest(Long adoptionId) {
        final var as = getById(adoptionId).orElseThrow(() -> new RuntimeException("No such adoption."));
        restClient.post()
                .uri("/adoptions/{adoptionId}/approve", adoptionId)
                .retrieve()
                .toBodilessEntity();

        petClient.markAdopted(as.pet().id());
    }

    public void rejectRequest(Long adoptionId) {
        restClient.post()
                .uri("/adoptions/{adoptionId}/reject", adoptionId)
                .retrieve()
                .toBodilessEntity();
    }
}
