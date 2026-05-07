package com.awbd.pawsy.client;

import com.awbd.pawsy.dto.UserCreateRequest;
import com.awbd.pawsy.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Slf4j
@Component
public class UserClient {
    private final RestClient restClient;

    public UserClient(@Value("${user.service.url}") String userServiceUrl) {
        log.info("UserClient registering at `{}`.", userServiceUrl);
        restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }

    public Optional<UserResponse> getByUsername(String username) {
        try {
            var response = restClient.get()
                    .uri("/users/by-name/{username}", username)
                    .retrieve()
                    .body(UserResponse.class);

            return Optional.ofNullable(response);
        } catch (HttpClientErrorException.NotFound ignored) {
            return Optional.empty();
        }
    }

    public void registerUser(UserCreateRequest dto) {
        log.info("Registering user `{}`", dto.username());
        restClient.post()
                .uri("/users")
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }
}
