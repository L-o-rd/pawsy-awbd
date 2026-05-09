package com.awbd.pawsy.client;

import com.awbd.pawsy.dto.AdoptionResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class AdoptionClient {
    private final RestClient restClient;

    public AdoptionClient(@Value("${adoption.service.url}") String adoptionServiceUrl) {
        log.info("AdoptionClient registering at `{}`.", adoptionServiceUrl);
        restClient = RestClient.builder()
                .baseUrl(adoptionServiceUrl)
                .build();
    }

    public List<AdoptionResponse> getRequestsForShelter(Long shelterId) {
        return List.of();
    }

    public Optional<AdoptionResponse> getById(Long adoptionId) {
        return Optional.empty();
    }

    public void approveRequest(Long adoptionId) {

    }

    public void rejectRequest(Long adoptionId) {

    }
}
