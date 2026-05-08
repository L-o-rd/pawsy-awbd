package com.awbd.pawsy.client;

import com.awbd.pawsy.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class UserClient {
    private final RestClient restClient;
    private final UserMapper userMapper;

    public UserClient(@Value("${user.service.url}") String userServiceUrl, UserMapper userMapper) {
        log.info("UserClient registering at `{}`.", userServiceUrl);
        restClient = RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();

        this.userMapper = userMapper;
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

    public Optional<UserUpdateRequest> getProfileForUpdate(String username) {
        return getByUsername(username).map(userMapper::toUpdateRequest);
    }

    public void update(String username, UserUpdateRequest dto) {
        restClient.put()
                .uri("/users/by-name/{username}", username)
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public void registerUser(UserCreateRequest dto) {
        log.info("Registering user `{}`", dto.username());
        restClient.post()
                .uri("/users")
                .body(dto)
                .retrieve()
                .toBodilessEntity();
    }

    public AdminStats getAdminStats() {
        return new AdminStats(0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L);
    }

    public List<?> getRecentReviews() {
        return List.of();
    }

    public void deleteReviewById(Long reviewId) {}
}
