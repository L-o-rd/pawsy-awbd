package com.awbd.pawsy.pet.dto;

import jakarta.validation.constraints.*;

public record ReviewCreateRequest(
    @NotBlank(message = "Reviews must contain a message.")
    @Size(min = 10, message = "Review message should be at least 10 characters.")
    String comment,

    @NotNull(message = "Reviews must have a rating.")
    @Min(value = 1, message = "Review rating should be between 1 and 5.")
    @Max(value = 5, message = "Review rating should be between 1 and 5.")
    Integer rating
) {}
