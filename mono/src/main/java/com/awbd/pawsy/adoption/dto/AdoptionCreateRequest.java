package com.awbd.pawsy.adoption.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdoptionCreateRequest(
    @NotBlank(message = "Adopters should leave a message.")
    @Size(min = 10, max = 100, message = "Leave a descriptive message between 10-100 characters.")
    String message
) {}
