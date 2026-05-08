package com.awbd.pawsy.client;

import com.awbd.pawsy.dto.PageResponse;
import com.awbd.pawsy.dto.PetResponse;
import com.awbd.pawsy.dto.ShelterResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Slf4j
@Component
public class PetClient {
    private final RestClient restClient;

    public PetClient(@Value("${pet.service.url}") String petServiceUrl) {
        log.info("PetClient registering at `{}`.", petServiceUrl);
        restClient = RestClient.builder()
                .baseUrl(petServiceUrl)
                .build();
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

    public List<ShelterResponse> allShelters() {
        final var type = new ParameterizedTypeReference<List<ShelterResponse>>() {};
        return restClient.get()
                .uri("/shelters")
                .retrieve()
                .body(type);
    }
}
