package com.awbd.pawsy.pet.dto;

import jakarta.validation.constraints.*;

public record ShelterCreateRequest(
    @NotBlank(message = "Shelters should have a name.")
    @Pattern(
            regexp = "^[\\p{L} .'-]+$",
            message = "Shelter name can only contain letters, spaces, and .'-")
    @Size(min = 2, max = 20, message = "Shelter name must be between 2 and 20 characters.")
    String name,

    @NotBlank(message = "Shelters should have a location.")
    String location,

    @NotBlank(message = "Shelters should have an e-mail.")
    @Email(message = "Invalid e-mail format.")
    String email,

    @NotBlank(message = "Shelters should have a phone number.")
    @Pattern(
            regexp = "^\\+?[0-9]{10,15}$",
            message = "Phone number must be 10–15 digits, optional +.")
    String phone
) {}
