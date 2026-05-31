package com.awbd.pawsy.client;

import com.awbd.pawsy.dto.*;
import com.awbd.pawsy.exception.AdoptionClientException;
import com.awbd.pawsy.exception.AdoptionDuplicateException;
import com.awbd.pawsy.exception.AppointmentStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
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

    public List<AppointmentResponse> getAppointmentsForAdopter(final String username) {
        final var type = new ParameterizedTypeReference<List<AppointmentSummary>>() {};
        var vass = restClient.get()
                .uri("/appointments/by-user/{username}", username)
                .retrieve()
                .body(type);

        final var ass = requireNonNull(vass);
        return ass.stream().map(as -> new AppointmentResponse(as.id(),
                petClient.getPetById(as.petId()).orElseThrow(),
                as.adopterName(),
                as.appointmentDate(),
                as.scheduledAtDate(),
                as.status())).toList();
    }

    public List<String> getBookedDatesFor(final Long petId) {
        final var type = new ParameterizedTypeReference<List<String>>() {};
        return restClient.get()
                .uri("/appointments/by-pet/{petId}/booked", petId)
                .retrieve()
                .body(type);
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

    public Optional<AppointmentResponse> getAppointmentById(Long appId) {
        try {
            var vas = restClient.get()
                    .uri("/appointments/{appId}", appId)
                    .retrieve()
                    .body(AppointmentSummary.class);

            final var as = requireNonNull(vas);
            return Optional.of(new AppointmentResponse(as.id(),
                    petClient.getPetById(as.petId()).orElseThrow(),
                    as.adopterName(),
                    as.appointmentDate(),
                    as.scheduledAtDate(),
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
                .onStatus(status -> status.value() == 400,
                    (req, res) -> {
                        throw new AdoptionDuplicateException("Adoption request already exists.");
                    })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            throw new AdoptionClientException("Adoption service has ran into an error.");
                        })
                .toBodilessEntity();
    }

    public void createAppointment(String username, Long petId, Long shelterId, AppointmentCreateRequest dto) {
        final var pet = petClient.getPetById(petId).orElseThrow();
        if (pet.status().equals("Adopted")) {
            log.error("Adopter `{}` tried to book an appointment for adopted pet `{}` on {}.", username, petId, dto.appointmentDate().toString());
            throw new AppointmentStateException("This pet has already been adopted!");
        }

        restClient.post()
                .uri("/appointments/for-pet/{petId}/at/{shelterId}/for-user/{username}",
                        petId, shelterId, username)
                .body(dto)
                .retrieve()
                .onStatus(status -> status.value() == 400,
                        (req, res) -> {
                            throw new AppointmentStateException("Could not make an appointment.");
                        })
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

    public void cancelAppointment(Long appId) {
        restClient.post()
                .uri("/appointments/{appId}/cancel", appId)
                .retrieve()
                .toBodilessEntity();
    }

    public AdoptionStats getStats() {
        return restClient.get()
                .uri("/stats")
                .retrieve()
                .body(AdoptionStats.class);
    }

    public Boolean isPetFree(Long petId) {
        return restClient.get()
                .uri("/stats/is-pet-free/{petId}", petId)
                .retrieve()
                .body(Boolean.class);
    }
}
